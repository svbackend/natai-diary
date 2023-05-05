<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use App\Common\Service\Env;
use App\Diary\Http\Request\UploadLinkImageRequest;
use App\Diary\Http\Response\UploadLinkImageResponse;
use Aws\S3\S3Client;
use Intervention\Image\ImageManager;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Link")
 */
class UploadLinkImageAction extends BaseAction
{
    public function __construct(
        private S3Client $s3Client,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=UploadLinkImageRequest::class))
     * @OA\Response(response=200, description="success", @Model(type=UploadLinkImageResponse::class))
     * @OA\Response(response=400, description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/links/upload-image', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] User    $user,
        UploadLinkImageRequest $request,
    ): HttpOutputInterface
    {
        $this->denyAccessUnlessGranted(User::ROLE_ADMIN);

        $imageContent = file_get_contents($request->imageUrl);

        if ($imageContent === false) {
            return $this->error('image_not_loaded');
        }

        // save image to tmp file
        $tmpImageFile = tempnam(sys_get_temp_dir(), 'link_image');
        file_put_contents($tmpImageFile, $imageContent);

        // resize image to 480×360
        $imageManager = new ImageManager();

        $imageStream = $imageManager
            ->make($tmpImageFile)
            ->orientate()
            ->fit(480, 360)
            ->stream('jpg');

        // upload image to s3 with public access
        $publicBucket = Env::getAwsUploadPublicBucket();
        $imageUniqId = uniqid();
        $folder = Env::isProd() ? 'prod' : 'dev';
        $imageKey = "$folder/links/$imageUniqId.jpg";

        // Grant public-read access to the object.
        $obj = $this->s3Client->putObject([
            'Bucket' => $publicBucket,
            'Key' => $imageKey,
            'Body' => $imageStream,
            'ACL' => 'public-read',
        ]);

        $imageUrl = $obj->get('ObjectURL');

        return new UploadLinkImageResponse(
            url: $imageUrl,
        );
    }
}
