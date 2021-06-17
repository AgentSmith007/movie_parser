package com.parser.movie.movie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;

@Service
@RequiredArgsConstructor
public class MovieService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieService.class);
    private static final String REQUEST_URL_MOVIE = "https://easy.test-assignment-a.loyaltyplant.net/3/discover/movie";
    private static final String REQUEST_API_KEY = "?api_key=$72b56103e43843412a992a8d64bf96e9";
    private static final String REQUEST_PAGE_PARAMETER = "&page=";
    private static final String PATTERN_URL_MOVIE = REQUEST_URL_MOVIE + REQUEST_API_KEY + REQUEST_PAGE_PARAMETER;

    @Getter
    private final Map<Long, MovieCalculation> requestIdAndMovieAverageMap = new HashMap<>();
    private final AtomicLong requestIdCounter = new AtomicLong();
    private final ObjectMapper mapper;

    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    public Long createRequestIdAndStartCalculationAverageGenre(Integer genreId) {
        Long requestId = requestIdCounter.incrementAndGet();
        MovieCalculation calculation = new MovieCalculation();
        requestIdAndMovieAverageMap.put(requestId, calculation);
        executor.submit(() ->
                calcAverageRatingByGenreId(genreId, requestIdAndMovieAverageMap.get(requestId)));
        return requestId;
    }

    public AverageGenreDto getCurrentComputingProgress(long requestId) {
        return mapCalculationToAverageGenreDto(requestIdAndMovieAverageMap.get(requestId));
    }

    public AverageGenreDto stopCalculating(long requestId) {
        MovieCalculation calculation = requestIdAndMovieAverageMap.get(requestId);
        calculation.setStopped(true);
        return mapCalculationToAverageGenreDto(calculation);
    }


    private double calcAverageRatingByGenreId(Integer genreId, MovieCalculation calculation) throws MalformedURLException {
        final double[] movieScoreSum = {0};
        final int[] movieTotalCount = {0};
        for (int pageId = 1; !calculation.isStopped(); pageId++) {
            var urlString = new URL(PATTERN_URL_MOVIE + pageId);
            var jsonObject = getJsonFromUrl(urlString);
            //вычитаем единицу, так как максимальная страница 1957 и по урлу с "total_pages":1958 ничего нет, пусто
            int totalPages = jsonObject.getInt("total_pages") - 1;
            parseMoviesFromJson(jsonObject).stream()
                    .filter(movie -> movie.getGenreIds().contains(genreId))
                    .forEach(movieDto -> {
                        movieScoreSum[0] += movieDto.getVoteAverage();
                        movieTotalCount[0]++;
                    });
            calculation.setProgress((double) pageId / (double) totalPages);
            if (pageId == totalPages) {
                calculation.setProgress(100);
                calculation.setFinished(true);
                break;
            }
        }
        double result = movieScoreSum[0] / movieTotalCount[0];
        calculation.setAverageRating(result);
        LOGGER.info("CALCULATION result : {}", result);
        return result;
    }

    private List<MovieDto> parseMoviesFromJson(JSONObject jsonObject) {
        Object results = jsonObject.get("results");
        try {
            return Arrays.asList(mapper.readValue(results.toString(), MovieDto[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private AverageGenreDto mapCalculationToAverageGenreDto(MovieCalculation calculation) {
        AverageGenreDto genreDto = new AverageGenreDto();
        genreDto.setProgress(calculation.getProgress());
        genreDto.setFinished(calculation.isFinished());
        genreDto.setStopped(calculation.isStopped());
        genreDto.setAverageRating(calculation.getAverageRating());
        return genreDto;
    }


    //TODO refactor method
    private JSONObject getJsonFromUrl(URL urlString) {
        StringBuilder sb = null;
        URL url;
        URLConnection urlCon;
        try {
            url = urlString;
            urlCon = url.openConnection();
            BufferedReader in = null;
            if (urlCon.getHeaderField("Content-Encoding") != null
                    && urlCon.getHeaderField("Content-Encoding").equals("gzip")) {
                LOGGER.info("reading data from URL as GZIP Stream");
                in = new BufferedReader(new InputStreamReader(new GZIPInputStream(urlCon.getInputStream())));
            } else {
                LOGGER.info("reading data from URL as InputStream");
                in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            }
            String inputLine;
            sb = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            LOGGER.info("Exception while reading JSON from URL - {}", e);
        }
        if (sb != null) {
            return new JSONObject(sb.toString());
        } else {
            LOGGER.warn("No JSON Found in given URL");
            return new JSONObject("");
        }
    }
}
