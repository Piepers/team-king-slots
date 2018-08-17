package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents the configuration of a reel which needs to be valid before a spin can be executed on the reel. It needs a 'from'
 * and 'to' (inclusive) number to let it know which random numbers can be present and it needs a list of
 * {@link CellSymbolConfig} to determine which symbols are valid for the random numbers.
 * <p>
 * It makes sure that all random numbers are assigned a {@link CellSymbolConfig}. No duplicates or overlap are allowed.
 *
 * @author Bas Piepers
 */
@DataObject
public class ReelConfig implements JsonDomainObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReelConfig.class);
    private final int from;
    private final int to;
    private List<CellSymbolConfig> cellConfig;

    private ReelConfig(final int from, final int to) {
        this.from = from;
        this.to = to;
    }

    public ReelConfig(JsonObject jsonObject) {
        this.from = jsonObject.getInteger("from");
        this.to = jsonObject.getInteger("to");
        JsonArray cellConfigArr = jsonObject.getJsonArray("cellConfig");
        if (Objects.nonNull(cellConfigArr)) {
            this.cellConfig = cellConfigArr.stream().map(o -> new CellSymbolConfig((JsonObject)o)).collect(Collectors.toList());
        }
//        this.cellConfig = jsonObject.getJsonArray("cellConfig").getList();
    }

    public static ReelConfig of(final int from, final int to) {
        if (from >= to) {
            throw new IllegalArgumentException("A reel configuration must have a valid starting and end number (start must be smaller than end number)");
        }

        // Only support ranges starting from 1
        if (from < 1) {
            throw new IllegalArgumentException("A reel configuration must start its range from a value larger than 1");
        }

        return new ReelConfig(from, to);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Business Logic ///////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Add a new configuration to this ReelConfig. Validates that the assigned numbers of the given
     * {@link CellSymbolConfig} do not already overlap an existing configuration.
     *
     * @param cellSymbolConfig, a configuration containing symbol and number configuration
     * @return this instance for fluent programming.
     */
    public ReelConfig addCellConfig(CellSymbolConfig cellSymbolConfig) {
        if (Objects.nonNull(cellConfig) && this.numberAssignmentAlreadyExists(cellSymbolConfig)) {
            throw new IllegalArgumentException("The cell configuration is not valid. One or more numbers are already mapped to other symbols.");
        }

        if (Objects.isNull(this.cellConfig)) {
            this.cellConfig = new ArrayList<>();
        }

        this.cellConfig.add(cellSymbolConfig);

        return this;
    }

    private boolean numberAssignmentAlreadyExists(CellSymbolConfig cellSymbolConfig) {
        return cellSymbolConfig.getNumberAssignment().stream().anyMatch(element -> this.getAssignedNumbers().contains(element));
    }


    @JsonIgnore
    public boolean isValid() {
        if (Objects.isNull(this.cellConfig) || this.cellConfig.size() == 0) {
            return false;
        }
        // No gaps, no overlap and spans entire range
        List<Integer> reference = IntStream.rangeClosed(from, to).sorted().boxed().collect(Collectors.toList());
        List<Integer> assignments = cellConfig.stream().flatMap(item -> item.getNumberAssignment().stream()).sorted().collect(Collectors.toList());

        if (!reference.equals(assignments)) {
            return false;
        }

        return true;
    }

    @JsonIgnore
    private List<Integer> getAssignedNumbers() {
        if (Objects.nonNull(this.cellConfig)) {
            return this.cellConfig.stream().flatMap(item -> item.getNumberAssignment().stream()).sorted().collect(Collectors.toList());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Retrieves the symbol that is assigned to the given number. Throws an exception if the given number is not
     * assigned. Finds the first symbol that is assign to this number ie. in case multiple numbers "have" this symbol
     * they are ignored (because we assume that the given number is coming from a cell that will get that number
     * assigned to it).
     *
     * @param number, the number for which to retrieve the assigned symbol.
     * @return the {@link me.piepers.king.domain.CellSymbolConfig.Symbol} for the given number.
     */
    @JsonIgnore
    public CellSymbolConfig.Symbol getSymbolByNumber(Integer number) {
        CellSymbolConfig config = this.cellConfig
                .stream()
                .filter(c -> c.hasAssignedNumber(number))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The given number is not assigned to any of the cells in this reel (" + number + ")."));
        return config.getSymbol();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// Accessors ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public List<CellSymbolConfig> getCellConfig() {
        return cellConfig;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// Equals, hashCode and toString ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReelConfig that = (ReelConfig) o;

        if (from != that.from) return false;
        if (to != that.to) return false;
        return cellConfig != null ? cellConfig.equals(that.cellConfig) : that.cellConfig == null;
    }

    @Override
    public int hashCode() {
        int result = from;
        result = 31 * result + to;
        result = 31 * result + (cellConfig != null ? cellConfig.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReelConfig{" +
                "from=" + from +
                ", to=" + to +
                ", cellConfig=" + cellConfig +
                '}';
    }
}
