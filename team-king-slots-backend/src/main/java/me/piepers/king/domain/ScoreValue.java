package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents how much an x amount of symbols are worth in terms of score.
 *
 * @author Bas Piepers
 *
 */
public class ScoreValue {

    @JsonValue
    private Integer value;

    private ScoreValue(Integer value) {
        this.value = value;
    }

    public static ScoreValue of(Integer value) {
        return new ScoreValue(value);

    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ScoreValue{" +
                "value=" + value +
                '}';
    }
}
