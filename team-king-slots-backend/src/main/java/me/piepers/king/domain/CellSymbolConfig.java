package me.piepers.king.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The configuration of cell symbols on a Slot. The CellSymbolConfig is a representation of a symbol on the {@link ReelCell}
 * with one or more random numbers. In the prototype implementation the symbol is represented by an Enum that is
 * inspired by a couple of real life symbols but can later on be part of information that comes from an external source.
 * <p>
 * The CellSymbolConfig is not aware of the random numbers that are used, it is the {@link Reel}'s responsibility to only
 * assign a random number once. Symbols and which random numbers they represent is part of the configuration of a slot.
 * <p>
 * The value that a symbol represents is configured in scoreValues. This holds a map with two numbers where key
 * represents the amount of symbols and the value represents the score that is bound to that amount. For example:
 * 3 symbols in a row can represent a score of 150, 4 a score of 200 and 5 a score of 250. The CellSymbolConfig is not aware
 * of the amount of columns as reel actually has. Its just a configuration item that is needed to initialize a slot.
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
        List list = scoreConfigMap.keySet().stream().map(key -> SubsequentSymbols.resolve(key)).collect(Collectors.toList());
        list.stream().forEach(item -> System.out.println(item.toString()));
        this.scoreValues = scoreConfigMap.keySet().stream().map(key -> SubsequentSymbols.resolve(key)).collect(Collectors.toMap(s -> s, s -> ScoreValue.of((Integer)scoreConfigMap.get(s.name()))));
    }

    private CellSymbolConfig(Symbol symbol, Integer[] numberAssignment, Map<SubsequentSymbols, ScoreValue> scoreValues) {
        this.symbol = symbol;
        this.numberAssignment = Arrays.asList(numberAssignment);
        this.scoreValues = scoreValues;
    }

    public static CellSymbolConfig of(Symbol symbol, int from, int to, Map<SubsequentSymbols, ScoreValue> scoreValues) {
        if (from >= to) {
            throw new IllegalArgumentException("From must be smaller than to.");
        }

        Integer[] range = IntStream.range(from, to).boxed().toArray(Integer[]::new);

        return new CellSymbolConfig(symbol, range, scoreValues);
    }

    public static CellSymbolConfig of(Symbol symbol, Map<SubsequentSymbols, ScoreValue> scoreValues, Integer... numbers) {
        if (Arrays.stream(numbers).allMatch(new HashSet<>()::add)) {
            return new CellSymbolConfig(symbol, numbers, scoreValues);
        } else {
            throw new IllegalArgumentException("The range of numbers need to be unique for a CellSymbolConfig.");
        }
    }

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
        SEVEN, BELL, BAR, TWO_BARS, THREE_BARS, EMPTY, IMAGE1, IMAGE2, IMAGE3, IMAGE4, CHAR1, CHAR2, CHAR3, CHAR4, CHAR5, CHAR6;


        public static Symbol resolve(String from) {
            String localFrom = from.toUpperCase();
            switch (localFrom) {
                case "SEVEN":
                    return Symbol.SEVEN;
                case "BELL":
                    return Symbol.BELL;
                case "BAR":
                    return Symbol.BAR;
                case "TWO_BARS":
                    return Symbol.TWO_BARS;
                case "THREE_BARS":
                    return Symbol.THREE_BARS;
                case "EMPTY":
                    return Symbol.EMPTY;
                case "IMAGE1":
                    return Symbol.IMAGE1;
                case "IMAGE2":
                    return Symbol.IMAGE2;
                case "IMAGE3":
                    return Symbol.IMAGE3;
                case "IMAGE4":
                    return Symbol.IMAGE4;
                case "CHAR1":
                    return Symbol.CHAR1;
                case "CHAR2":
                    return Symbol.CHAR2;
                case "CHAR3":
                    return Symbol.CHAR3;
                case "CHAR4":
                    return Symbol.CHAR4;
                case "CHAR5":
                    return Symbol.CHAR5;
                case "CHAR6":
                    return Symbol.CHAR6;
                default:
                    throw new IllegalArgumentException("Invalid value for symbol: " + from);
            }
        }
    }
}
