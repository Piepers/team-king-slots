package me.piepers.king.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ReelTest {

//    @Test
//    public void test_instantiation_of_reel() {
//        Reel reel = new Reel(3, 5);
    // For testing purpose fill the reel with some bogus random numbers:
//        for(int i = 0; i< reel.getCells().length;i++) {
//            int[] row = reel.getCells()[i];
//            System.out.println("Amount of rows: " + row.length);
//
//        }
//        int amount = reel.getCellAmount();
//        System.out.println(amount);
//        JsonObject test = JsonObject.mapFrom(reel);
//        System.out.println(test.encodePrettily());
//    }

    @Test
    public void test_how_is_2d_json_mapped() {
        String anotherJson = "[[\"1\",\"100\"],[\"2\",\"200\"],[\"3\",\"300\"]]";
        String json = "{\"data\":[" +
                "[1,2,3]," +
                "[3,4,5]," +
                "[6,7,8]" +
                "]}";
        JsonArray jsonArray = new JsonArray(anotherJson);
        JsonObject jsonObject = new JsonObject(json);
        System.out.println(jsonArray);
        System.out.println(jsonObject);
        JsonArray anotherJsonArray = jsonObject.getJsonArray("data");
        anotherJsonArray
                .stream()
                .map(a -> (JsonArray) a)
                .forEach(a -> {
                    a.stream().forEach(i -> System.out.println(i));
                });

        TestDataObjectWithJsonArray test = new TestDataObjectWithJsonArray(jsonObject);
        int[][] theArray = test.getArray();
        Arrays
                .stream(theArray)
                .forEach(ar -> Arrays
                        .stream(ar)
                        .forEach(ia -> System.out.println("The mapped array is: " + ia)));

        DataObjectWithListOfList anotherTest = new DataObjectWithListOfList(jsonObject);
        System.out.println(anotherTest.toJson().encodePrettily());
//        anotherTest
//                .getRows()
//                .stream()
//                .peek(row -> System.out.println("Row peek: " + row))
//                .forEach(row -> row
//                        .stream()
//                        .peek(reelCell -> System.out.println("Cell peek: " + reelCell))
//                        .forEach(cell -> System.out.println(cell)));

//        anotherTest
//                .getRows()
//                .stream()
//                .forEach(cells -> cells
//                        .stream()
//                        .forEach(i ->
//                                System.out.println("We have: " + i.getValue())));

    }

    @DataObject
    class TestDataObjectWithJsonArray implements JsonDomainObject {
        // Represents the 2D array of the grid.
        private final JsonArray array;

        public TestDataObjectWithJsonArray(JsonObject jsonObject) {
            this.array = jsonObject.getJsonArray("data");
        }

        @JsonIgnore
        @GenIgnore
        public int[][] getArray() {
            int[][] ar = new int[array.size()][];
            for (int i = 0; i < array.size(); i++) {
                JsonArray inner = array.getJsonArray(i);
                int[] ip = new int[inner.size()];
                for (int j = 0; j < inner.size(); j++) {
                    ip[j] = inner.getInteger(j);
                }
                ar[i] = ip;
            }
            return ar;
        }
    }

    @DataObject
    class DataObjectWithListOfList implements JsonDomainObject {
        @JsonUnwrapped
        private final List<List<ReelCell>> rows;

        public DataObjectWithListOfList(JsonObject jsonObject) {
            JsonArray jsonArray = jsonObject.getJsonArray("data");
            this.rows = new ArrayList<>();
            // Now we have an array with elements that contain another JsonArray
            // FIXME: how to fix this in a way we don't have to explicitly cast raw types?
            jsonArray
                    .stream()
                    .forEach(o -> {
                        List<ReelCell> row =
                                ((JsonArray) o)
                                        .stream()
                                        .map(item -> new ReelCell((Integer) item))
                                        .collect(Collectors.toList());
                        rows.add(row);
                    });

//            jsonArray.getList().stream().map(row -> )

//            jsonArray
//                    .stream()
//                    .map(row -> (JsonArray)row)
//                    .forEach(row -> row
//                            .stream()
//                            .map(item -> (Integer) item)
//                            .map(i -> new ReelCell(i))
//                            .collect(Collectors.toList()));
//            jsonArray.getList().stream().collect(() -> new ArrayList<List<ReelCell>>(), )
//            jsonArray.getList().stream().peek(o -> System.out.println(o.getClass())).collect(Collector.of());
//                    .forEach(o -> System.out.println(o));
//            jsonArray.getList().stream().collect(Collector.of(() -> new ArrayList<List<ReelCell>>(), (list1, list2)-> , ));
//            jsonArray.getList().stream().collect(new ArrayList<List<ReelCell>>(), (o, o2) -> )
//            jsonArray.<List<List<ReelCell>>>getList().stream().forEach();
//            this.rows = null;
//            jsonArray
//                    .getList()
//                    .stream()
//                    .peek(o -> System.out.println("The object is: " + o + ", class: " + o.getClass()))

//            this.rows = jsonArray.getList().stream().collect(Collectors.toList());
//            jsonArray.getList().stream().forEach(o -> System.out.println("The object is now: " + o));
//            this.rows = jsonArray.getList();
//            this.array = jsonObject.getJsonArray("data");
        }

        public List<List<ReelCell>> getRows() {
            return rows;
        }
    }
}

