<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Billing\Entity\UserFeature;
use App\Billing\Entity\UserOrder;
use App\Billing\Entity\UserOrderFeature;
use App\Billing\Exception\EmptyClientSecretException;
use App\Billing\Exception\EmptyEphemeralKeyException;
use App\Billing\Service\PaymentGateway;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Diary\Http\Response\BuyFeatureResponse;
use App\Diary\OpenApi\Ref\BuySuggestionLinksErrorRef;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Link")
 */
class BuySuggestionLinksAction extends BaseAction
{
    public function __construct(
        private PaymentGateway         $paymentGateway,
        private EntityManagerInterface $em,
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success", @Model(type=BuyFeatureResponse::class))
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=422, description="checkout session error", @Model(type=BuySuggestionLinksErrorRef::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/links/buy', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] User $user,
    ): HttpOutputInterface
    {
        try {
            $checkoutSession = $this->paymentGateway->createSuggestionLinksCheckoutSession($user);
        } catch (EmptyEphemeralKeyException) {
            return $this->error(BuySuggestionLinksErrorRef::EMPTY_EPHEMERAL_KEY);
        } catch (EmptyClientSecretException) {
            return $this->error(BuySuggestionLinksErrorRef::EMPTY_PAYMENT_INTENT_CLIENT_SECRET);
        }

        $userOrder = new UserOrder(
            user: $user,
            stripeSessionId: $checkoutSession->id
        );

        $suggestionLinksFeature = new UserOrderFeature(
            order: $userOrder,
            feature: UserFeature::FEAT_SUGGESTION_LINKS,
            price: PaymentGateway::PRICE_SUGGESTION_LINKS,
        );

        $this->em->persist($userOrder);
        $this->em->persist($suggestionLinksFeature);
        $this->em->flush();

        return new BuyFeatureResponse(
            customerId: $checkoutSession->stripeCustomerId,
            ephemeralKey: $checkoutSession->ephemeralKey,
            paymentIntentSecret: $checkoutSession->paymentIntentSecret,
        );
    }
}
