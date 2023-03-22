<?php

namespace App\Common\Controller;

use App\Common\Entity\Feedback;
use App\Common\Http\Request\FeedbackRequest;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\Http\Response\StaticContentResponse;
use App\Common\Repository\FeedbackRepository;
use App\Common\Service\MailerService;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;

class FeedbackAction extends BaseAction
{
    public function __construct(
        private MailerService $mailerService,
        private FeedbackRepository $feedbackRepository,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=FeedbackRequest::class))
     * @OA\Response(response=204, description="success")
     */
    #[Route('/api/v1/feedback', name: 'feedback', methods: ['POST'])]
    public function __invoke(FeedbackRequest $request): HttpOutputInterface
    {
        $user = $this->getUser();
        $feedback = new Feedback($request->content, $request->stars, $user);
        $this->feedbackRepository->save($feedback, flush: true);

        $this->mailerService->sendFeedback($feedback);

        return $this->success();
    }
}
