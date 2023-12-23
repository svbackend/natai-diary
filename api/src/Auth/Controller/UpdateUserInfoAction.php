<?php

namespace App\Auth\Controller;

use App\Auth\Entity\User;
use App\Auth\Entity\UserProfile;
use App\Auth\Http\Request\UpdateUserRequest;
use App\Auth\Repository\UserRepository;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use App\Location\Repository\CityRepository;
use App\Tests\Functional\Auth\Controller\UpdateUserInfoActionTest;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Auth")
 * @see UpdateUserInfoActionTest
 */
class UpdateUserInfoAction extends BaseAction
{
    public function __construct(
        private CityRepository $cities,
        private UserRepository $users,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=UpdateUserRequest::class))
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=401, description="auth required", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=404, description="city_not_found", @Model(type=NotFoundErrorRef::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/me', methods: ['PUT'])]
    public function __invoke(
        UpdateUserRequest $request,
        #[CurrentUser] User $user,
    ): HttpOutputInterface
    {
        $city = $this->cities->find($request->cityId);

        if (!$city) {
            return $this->error('city_not_found', Response::HTTP_NOT_FOUND);
        }

        $user->update(
            name: $request->name,
        );

        $profile = $user->getProfile();

        if ($profile) {
            $profile->update(
                city: $city,
                timezoneOffset: $request->timezoneOffset,
                enableEmailNotifications: $request->enableEmailNotifications,
            );
        } else {
            $profile = new UserProfile(
                city: $city,
                timezoneOffset: $request->timezoneOffset,
                enableEmailNotifications: $request->enableEmailNotifications,
            );
            $user->setProfile($profile);
        }

        $this->users->save($user, flush: true);

        return $this->success();
    }
}