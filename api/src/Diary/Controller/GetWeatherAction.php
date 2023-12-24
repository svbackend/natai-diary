<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Exception\ValidationException;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use App\Diary\Http\Response\LoadLinkResponse;
use App\Diary\Http\Response\WeatherResponse;
use App\Diary\Service\OpenWeatherService;
use App\Location\Repository\CityRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Psr\Log\LoggerInterface;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Validator\ValidatorInterface;

/**
 * @OA\Tag(name="Diary")
 */
class GetWeatherAction extends BaseAction
{
    public function __construct(
        private ValidatorInterface $validator,
        private OpenWeatherService $weatherService,
        private CityRepository $cities,
        private LoggerInterface $logger,
    )
    {
    }

    /**
     *
     * @OA\Response(response=200, description="success", @Model(type=WeatherResponse::class))
     * @OA\Response(response=400, description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=404, description="city not found", @Model(type=NotFoundErrorRef::class))
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/weather/{cityId}/{date}', methods: ['GET'])]
    public function __invoke(
        #[CurrentUser] User $user,
        int $cityId,
        string $date, // Y-m-d
    ): HttpOutputInterface
    {
        // validate input
        $rules = new Assert\Collection([
            'cityId' => [new Assert\Positive(),],
            'date' => [new Assert\Date(),],
        ]);

        $errors = $this->validator->validate([
            'cityId' => $cityId,
            'date' => $date,
        ], $rules);

        if (count($errors) > 0) {
            throw new ValidationException($errors);
        }

        $city = $this->cities->find($cityId);

        if (!$city) {
            return $this->error('city_not_found');
        }

        // weather code icon - https://openweathermap.org/weather-conditions#Icon-list
        $weatherIcon = $this->weatherService->getWeatherIcon(
            coordinates: $city->getCoordinates(),
            date: new \DateTime($date)
        );

        // 10 - Sunny / Clear
        // 9 - Cloudy & sunny
        // 8 - Cloudy & barely sunny
        // 7 - Cloudy
        // 6 - Cloudy & rainy
        // 5 - Rainy
        // 4 - Stormy
        // 3 - Snowy
        $codesMap = [
            '01d' => 10,
            '01n' => 10,
            '02d' => 9,
            '02n' => 9,
            '03d' => 8,
            '03n' => 8,
            '04d' => 7,
            '04n' => 7,
            '09d' => 6,
            '09n' => 6,
            '10d' => 5,
            '10n' => 5,
            '11d' => 4,
            '11n' => 4,
            '13d' => 3,
            '13n' => 3,
            '50d' => 7,
            '50n' => 7,
        ];

        if (isset($codesMap[$weatherIcon])) {
            $mappedCode = $codesMap[$weatherIcon];
        } else {
            $this->logger->error('Unknown weather code id', [
                'code' => $weatherIcon,
            ]);
            $mappedCode = 9;
        }

        return new WeatherResponse(
            code: $mappedCode,
        );
    }
}
