package com.parser.movie.movie;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
class MovieServiceTest {
    @Autowired
    private MovieService movieService;


    @Before
    public void setUp() {
        when(movieService.createRequestIdAndStartCalculationAverageGenre(any())).thenReturn(1L);

    }

    @Test
    public void step01_CalculateAverageRating() {
        Long requestId = movieService.createRequestIdAndStartCalculationAverageGenre(80);
    }

}