package me.piepers.king.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The configuration of cell symbols on a Slot. The CellSymbolConfig is a representation of a symbol on the
 * {@link ReelCell} with one or more random numbers. In the prototype implementation the symbol is represented by an
 * Enum that is inspired by a couple of real life symbols but can later on be part of information that comes from an
 * external source.
 * <p>
 * The CellSymbolConfig is not aware of the random numbers that are used, it is the {@link Reel}'s responsibility to
 * only assign a random number once. Symbols and which random numbers they represent is part of the configuration of a
 * slot.
 * <p>
 * The value that a symbol represents is configured in scoreValues. This holds a map with two numbers where key
 * represents the amount of symbols and the value represents the score that is bound to that amount. For example:
 * 3 symbols in a row can represent a score of 150, 4 a score of 200 and 5 a score of 250. The CellSymbolConfig is not
 * aware of the amount of columns a reel actually has. Its just a configuration item that is needed to initialize a slot.
 *
 * @author Bas Piepers
 */
@DataObject
public class CellSymbolConfig implements JsonDomainObject {

    private final Symbol symbol;
    // Must be a list due to JsonObject limitations.
    private final List<Integer> numberAssignment;
    private final Map<SubsequentSymbols, ScoreValue> scoreValues;

    public CellSymbolConfig(JsonObject jsonObject) {
        this.symbol = Symbol.resolve(jsonObject.getString("symbol"));
        this.numberAssignment = jsonObject.getJsonArray("numberAssignment").getList();
        JsonObject scoreConfig = jsonObject.getJsonObject("scoreValues");
        Map<String, Object> scoreConfigMap = scoreConfig.getMap();
        // Map a String, Object pair to a SubsequentSumbols, ScoreValue pair.
        this.scoreValues = scoreConfigMap
                .keySet()
                .stream()
                .map(key -> SubsequentSymbols.resolve(key))
                .collect(Collectors
                        .toMap(s -> s,
                                s -> ScoreValue.of((Integer) scoreConfigMap.get(s.name()))));
    }

    private CellSymbolConfig(Symbol symbol, Integer[] numberAssignment, Map<SubsequentSymbols, ScoreValue> scoreValues) {
        this.symbol = symbol;
        this.numberAssignment = Arrays.asList(numberAssignment);
        this.scoreValues = scoreValues;
    }

    /**
     * Factory method that allows to set a symbol with a range (from and to) of random numbers for which the given
     * symbol is chosen and the score configuration. The scoreValues represent how many {@link SubsequentSymbols} a
     * given {@link ScoreValue} represents.
     *
     * @param symbol,      the symbol to configure for a cell.
     * @param from,        the starting value of the random number (inclusive) for which this symbol is valid.
     * @param to,          the end value of the random number (excluding) for which this symbol is valid.
     * @param scoreValues, the scorevalues configuration. Ie. how many points a specific subsequent set of symbols
     *                     represent.
     * @return an instance of {@link CellSymbolConfig} that can be used in a slot to calculate the score of a spin.
     */
    public static CellSymbolConfig of(Symbol symbol, int from, int to, Map<SubsequentSymbols, ScoreValue> scoreValues) {
        if (from >= to) {
            throw new IllegalArgumentException("From must be smaller than to.");
        }

        Integer[] range = IntStream.range(from, to).boxed().toArray(Integer[]::new);

        return new CellSymbolConfig(symbol, range, scoreValues);
    }

    /**
     * A factory method that sets a specific set of numbers to a {@link Symbol} and a score configuration.
     *
     * @param symbol,      the symbol to configure for a cell.
     * @param scoreValues, the score values configuration. Ie. how many points a specific subsequent set of symbols
     *                     represent.
     * @param numbers,     one or more numbers for which this symbol configuration applies.
     * @return an instance of {@link CellSymbolConfig} that can be used in a slot to calculate the score of a spin.
     */
    public static CellSymbolConfig of(Symbol symbol, Map<SubsequentSymbols, ScoreValue> scoreValues, Integer... numbers) {
        if (Objects.isNull(numbers) || numbers.length == 0) {
            throw new IllegalArgumentException("Expecting at least one number to configure this instance.");
        }
        if (Arrays.stream(numbers).allMatch(new HashSet<>()::add)) {
            return new CellSymbolConfig(symbol, numbers, scoreValues);
        } else {
            throw new IllegalArgumentException("The range of numbers need to be unique for a CellSymbolConfig.");
        }
    }

    /**
     * A factory method that sets one specific number to a {@link Symbol} and a score configuration.
     *
     * @param symbol,      the symbol to configure for a cell.
     * @param scoreValues, the score values configuration. Ie. how many points a specific subsequent set of symbols
     *                     represent.
     * @param number,      one number for which this symbol configuration applies.
     * @return an instance of {@link CellSymbolConfig} that can be used in a slot to calculate the score of a spin.
     */
    public static CellSymbolConfig of(Symbol symbol, Map<SubsequentSymbols, ScoreValue> scoreValues, int number) {
        return new CellSymbolConfig(symbol, new Integer[]{number}, scoreValues);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<Integer> getNumberAssignment() {
        return numberAssignment;
    }

    public Map<SubsequentSymbols, ScoreValue> getScoreValues() {
        return scoreValues;
    }

    public Integer getScoreValueFor(SubsequentSymbols key) {
        return Objects.nonNull(this.scoreValues) && this.scoreValues.containsKey(key) ?
                this.scoreValues.get(key).getValue() : this.scoreValues.get(key).getValue();
    }

    // Convenient methods.
    public static Map<SubsequentSymbols, ScoreValue> symbolScores(SubsequentSymbols[] subsequentSymbols, Integer[] value) {
        if (Objects.isNull(subsequentSymbols) || Objects.isNull(value) || subsequentSymbols.length < value.length) {
            throw new IllegalArgumentException("Invalid arguments for symbolScores");
        }

        Map<SubsequentSymbols, ScoreValue> symbolconfig = new HashMap<>(subsequentSymbols.length);
        for (int i = 0; i < subsequentSymbols.length; i++) {
            symbolconfig.put(subsequentSymbols[i], ScoreValue.of(value[i]));
        }
        return Collections.unmodifiableMap(symbolconfig);
    }

    @Override
    public String toString() {
        return "CellSymbolConfig{" +
                "symbol=" + symbol +
                ", numberAssignment=" + numberAssignment +
                ", scoreValues=" + scoreValues +
                '}';
    }

    enum Symbol {
        NONE, SEVEN, BELL, BAR, TWO_BARS, THREE_BARS, EMPTY, IMAGE1, IMAGE2, IMAGE3, IMAGE4, CHAR1, CHAR2, CHAR3, CHAR4, CHAR5, CHAR6;


        public static Symbol resolve(String from) {
            String localFrom = from.toUpperCase();
            switch (localFrom) {
                case "SEVEN":
                    return SEVEN;
                case "BELL":
                    return BELL;
                case "BAR":
                    return BAR;
                case "TWO_BARS":
                    return TWO_BARS;
                case "THREE_BARS":
                    return THREE_BARS;
                case "EMPTY":
                    return EMPTY;
                case "IMAGE1":
                    return IMAGE1;
                case "IMAGE2":
                    return IMAGE2;
                case "IMAGE3":
                    return IMAGE3;
                case "IMAGE4":
                    return IMAGE4;
                case "CHAR1":
                    return CHAR1;
                case "CHAR2":
                    return CHAR2;
                case "CHAR3":
                    return CHAR3;
                case "CHAR4":
                    return CHAR4;
                case "CHAR5":
                    return CHAR5;
                case "CHAR6":
                    return CHAR6;
                default:
                    return NONE;
            }
        }
    }
}
