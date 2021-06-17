package com.parser.movie.movie;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class MovieCalculation {
    private volatile boolean stopped;

    private double averageRating;
    private double progress;
    private boolean finished;

}
