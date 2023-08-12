<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Billing\Entity\UserFeature;
use App\Billing\Entity\UserOrder;
use App\Billing\Entity\UserOrderFeature;
use App\Billing\Service\PaymentGateway;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Diary\Http\Response\BuyFeatureResponse;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;
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
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/links/buy', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] User $user,
    ): HttpOutputInterface
    {
        $stripeCustomerId = $user->getStripeCustomerId() ?: $this->paymentGateway->createCustomer($user);

        $ephemeralKey = $this->paymentGateway->createEphemeralKey($stripeCustomerId);

        $checkoutSession = $this->paymentGateway->createSuggestionLinksCheckoutSession();

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
            checkoutUrl: $checkoutSession->url,
            ephemeralKey: $ephemeralKey,
            paymentIntentSecret: $checkoutSession->paymentIntentSecret,
            customerId: $stripeCustomerId,
        );
    }
}
