package com.parser.movie.movie;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movie/genre")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService cinemaService;

    @ApiOperation(value = "Запуск расчета среднего рейтинга всех фильмов по жанру, возвращает ID запроса")
    @GetMapping("/start-calculate-average")
    public Long startCalculationRating(@RequestParam int genreId) {
        System.out.println("Downloading started.............................");
        return cinemaService.createRequestIdAndStartCalculationAverageGenre(genreId);
    }

    @ApiOperation(value = "Получение прогресса расчета по ID запроса")
    @GetMapping("/progress")
    public AverageGenreDto getCurrentProgress(@RequestParam long requestId) {
        System.out.println("Get progress with request ID: " + requestId);
        return cinemaService.getCurrentComputingProgress(requestId);
    }

    @ApiOperation(value = "Остановка расчета по ID запроса")
    @GetMapping("/stop")
    public AverageGenreDto stopCalculating(@RequestParam long requestId) {
        System.out.println("Stop calculation with request ID: " + requestId);
        return cinemaService.stopCalculating(requestId);
    }


}
