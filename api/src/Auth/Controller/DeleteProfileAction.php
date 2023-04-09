<?php

namespace App\Auth\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ServerErrorRef;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Psr\Log\LoggerInterface;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Auth")
 * @see VerifyEmailActionTest
 */
class DeleteProfileAction extends BaseAction
{
    public function __construct(
        private EntityManagerInterface $em,
        private LoggerInterface $logger
    )
    {
    }

    /**
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/me', methods: ['DELETE'])]
    public function __invoke(
        #[CurrentUser] User $user
    ): HttpOutputInterface
    {
        $conn = $this->em->getConnection();

        $conn->beginTransaction();

        $randomEmail = uniqid('deleted-') . '@' . uniqid('deleted-') . '.com';

        try {
            $conn->delete('api_token', ['user_id' => $user->getId()]);
            $conn->update('users', ['email' => $randomEmail, 'name' => $randomEmail], ['id' => $user->getId()]);
            $conn->update('note', ['title' => '[deleted]', 'content' => '[deleted]'], ['user_id' => $user->getId()]);

            $conn->commit();
        } catch (\Exception $e) {
            $conn->rollBack();

            $this->logger->error($e->getMessage(), $e->getTrace());
            return $this->error('server_error', Response::HTTP_INTERNAL_SERVER_ERROR);
        }

        return $this->success();
    }
}