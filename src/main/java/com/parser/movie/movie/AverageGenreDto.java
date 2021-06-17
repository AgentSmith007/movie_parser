package com.parser.movie.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AverageGenreDto {
    @JsonProperty("average_rating")
    private Double averageRating;
    @JsonProperty("progress")
    private volatile double progress;
    @JsonProperty("stopped")
    private volatile boolean stopped;
    @JsonProperty("finished")
    private volatile boolean finished;

}
