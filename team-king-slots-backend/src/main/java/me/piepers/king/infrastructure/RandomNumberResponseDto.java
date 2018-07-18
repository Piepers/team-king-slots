package me.piepers.king.infrastructure;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import me.piepers.king.domain.JsonDomainObject;

import java.util.List;
import java.util.UUID;

/**
 * Inspired by the random.org service this Dto represents the responses of these kinds of services. Is used by all
 * implementations of the {@link RandomNumberService} interface but may be populated differently.
 *
 * @author Bas Piepers
 */
@DataObject
public class RandomNumberResponseDto implements JsonDomainObject {
    private final String id;
    private final Integer bitsUsed;
    private final Integer bitsLeft;
    private final Integer requestsLeft;
    private final List<Integer> data;


    public RandomNumberResponseDto(String id, Integer bitsUsed, Integer bitsLeft, Integer requestsLeft, List<Integer> data) {
        this.id = id;
        this.bitsUsed = bitsUsed;
        this.bitsLeft = bitsLeft;
        this.requestsLeft = requestsLeft;
        this.data = data;
    }

    public RandomNumberResponseDto(JsonObject jsonObject) {
        this.id = jsonObject.getString("id");
        this.bitsUsed = jsonObject.getInteger("bitsUsed");
        this.bitsLeft = jsonObject.getInteger("bitsLeft");
        this.requestsLeft = jsonObject.getInteger("requestsLeft");
        this.data = jsonObject.getJsonArray("data").getList();
    }

    public static RandomNumberResponseDto local(List<Integer> data) {
        return new RandomNumberResponseDto(UUID.randomUUID().toString(), 0, Integer.MAX_VALUE, Integer.MAX_VALUE, data);
    }

    public String getId() {
        return id;
    }

    public Integer getBitsUsed() {
        return bitsUsed;
    }

    public Integer getBitsLeft() {
        return bitsLeft;
    }

    public Integer getRequestsLeft() {
        return requestsLeft;
    }

    public List<Integer> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RandomNumberResponseDto that = (RandomNumberResponseDto) o;

        if (!id.equals(that.id)) return false;
        if (!bitsUsed.equals(that.bitsUsed)) return false;
        if (!bitsLeft.equals(that.bitsLeft)) return false;
        if (!requestsLeft.equals(that.requestsLeft)) return false;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + bitsUsed.hashCode();
        result = 31 * result + bitsLeft.hashCode();
        result = 31 * result + requestsLeft.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RandomNumberResponseDto{" +
                "id=" + id +
                ", bitsUsed=" + bitsUsed +
                ", bitsLeft=" + bitsLeft +
                ", requestsLeft=" + requestsLeft +
                '}';
    }
}
