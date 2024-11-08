package com.spruhs.donotbeangry.domain;

import com.spruhs.donotbeangry.domain.field.*;
import com.spruhs.donotbeangry.domain.player.Player;
import com.spruhs.donotbeangry.domain.player.Players;

import java.util.*;
import java.util.stream.Collectors;

public class StandardPlayingField implements PlayingField {

    private static final int BLUE_ENTRANCE_ID = 0;
    private static final int BLUE_EXIT_ID = 39;
    private static final int GREEN_ENTRANCE_ID = 10;
    private static final int GREEN_EXIT_ID = 9;
    private static final int RED_ENTRANCE_ID = 20;
    private static final int RED_EXIT_ID = 19;
    private static final int YELLOW_ENTRANCE_ID = 30;
    private static final int YELLOW_EXIT_ID = 29;

    private final List<Field> fields;

    public StandardPlayingField(Players players) {
        fields = new ArrayList<>();
        int counter = 0;

        counter = initBaseField(counter);

        counter = initAllHomeFields(counter, List.of(
                BLUE_EXIT_ID,
                GREEN_EXIT_ID,
                RED_EXIT_ID,
                YELLOW_EXIT_ID
        ));

        counter = initBaseFields(counter, getField(BLUE_ENTRANCE_ID));
        counter = initBaseFields(counter, getField(GREEN_ENTRANCE_ID));
        counter = initBaseFields(counter, getField(RED_ENTRANCE_ID));
        initBaseFields(counter, getField(YELLOW_ENTRANCE_ID));

        for (Player player : players.players()) {
            putFiguresOnField(player.color());
        }
    }

    private int initAllHomeFields(int counter, List<Integer> homeFieldIds) {
        for (int homeFieldId : homeFieldIds) {
            counter = initHomeFields(counter, getField(homeFieldId));
        }
        return counter;
    }

    private int initHomeFields(int counter, Field field) {
        if (!(field instanceof ExitField exitField)) {
            throw new IllegalStateException("Field with id " + field.getId() + " is not an ExitField");
        }
        HomeField homeField = createColorfulField(HomeField.class, exitField.getColor(), counter++);
        exitField.setHomeField(homeField);
        for (int i = 0; i < 3; i++) {
                HomeField nextHomeField = createColorfulField(HomeField.class, exitField.getColor(), counter++);
                homeField.setNextField(nextHomeField);
                homeField = nextHomeField;
        }
            return counter;

    }

    private <T extends ColorfulField> T createColorfulField(Class<T> colorfulField, Color color, int counter) {
        T field;
        try {
            field = colorfulField.getDeclaredConstructor(Color.class).newInstance(color);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        field.setId(counter);
        fields.add(field);
        return field;
    }

    private int initBaseField(int counter) {
        Field actualField = new EntranceField(Color.BLUE);
        for (int i = 0; i < 40; i++) {

            Field nextField = switch (i + 1) {
                case GREEN_EXIT_ID -> new ExitField(Color.GREEN);
                case GREEN_ENTRANCE_ID -> new EntranceField(Color.GREEN);
                case RED_EXIT_ID -> new ExitField(Color.RED);
                case RED_ENTRANCE_ID -> new EntranceField(Color.RED);
                case YELLOW_EXIT_ID -> new ExitField(Color.YELLOW);
                case YELLOW_ENTRANCE_ID -> new EntranceField(Color.YELLOW);
                case BLUE_EXIT_ID -> new ExitField(Color.BLUE);
                default -> new StandardField();
            };

            actualField.setNextField(nextField);
            actualField.setId(counter++);
            fields.add(actualField);
            actualField = nextField;
        }
        getField(BLUE_EXIT_ID).setNextField(getField(BLUE_ENTRANCE_ID));
        return counter;
    }


    private int initBaseFields(int counter, Field field) {
        if (!(field instanceof EntranceField entranceField)) {
            throw new IllegalArgumentException("Field with id " + field.getId() + " is not an EntranceField");
        }
        for (int i = 0; i < 4; i++) {
            BaseField baseField = createColorfulField(BaseField.class, entranceField.getColor(), counter++);
            baseField.setNextField(entranceField);
        }
        return counter;
    }

    public Field getField(int id) {
        if (id < 0 || id >= fields.size()) {
            throw new IllegalArgumentException("Field with id " + id + " does not exist");
        }
        return fields.get(id);
    }

    private void putFiguresOnField(Color color) {
        for (int i = 0; i < 4; i++) {
            placeFigureInBase(new Figure(color));
        }
    }

    public Optional<Color> winner() {
        return createColorCount().entrySet().stream()
                .filter(entry -> entry.getValue() == 4)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private EnumMap<Color, Long> createColorCount() {
        return fields.stream()
                .filter(field -> field instanceof HomeField homeField && !homeField.isEmpty())
                .collect(Collectors.groupingBy(
                        field -> ((HomeField) field).getColor(),
                        () -> new EnumMap<>(Color.class),
                        Collectors.counting()
                ));
    }


    public List<Action> possibleActions(Player player, int roll) {
        List<Action> result = new LinkedList<>();

        List<Field> figurePositions = getFigureFields(player);

        //if (figurePositions.size() != 4) {
        //    throw new IllegalStateException("Player " + player.color() + " has not placed all figures on the field");
        //}

        if (roll == 6) {
            createActionsWhenRollSix(player, roll, figurePositions, result);
        }

        if (result.isEmpty()) {
            createActionForStandardFields(player, roll, figurePositions, result);
        }

        return result;
    }

    private void createActionsWhenRollSix(Player player, int roll, List<Field> figurePositions, List<Action> result) {
        if (isFigureInBase(player, figurePositions) && !isFigureInEntrance(player, figurePositions)) {
            createActionForBaseFigure(roll, figurePositions, result);
        } else if (isFigureInBase(player, figurePositions)) {
            createActionForEntranceField(player, roll, result);
        }
    }

    private void createActionForStandardFields(Player player, int roll, List<Field> figurePositions, List<Action> result) {
        for (Field figureField : figurePositions) {
            if (!(figureField instanceof BaseField)) {
                calculateAction(player, roll, figureField, result);
            }
        }
    }

    private void createActionForEntranceField(Player player, int roll, List<Action> result) {
        Field entranceField = getEntrance(player.color());
        Field destinationField = getDestination(entranceField);
        if (destinationField.isEmpty() || isSameColor(player, destinationField)) {
            result.add(new Action(entranceField.getPlacedFigure(), entranceField, destinationField, roll));
        }
    }

    private boolean isSameColor(Player player, Field field) {
        return field.getPlacedFigure().color() != player.color();
    }

    private void calculateAction(Player player, int roll, Field figureField, List<Action> result) {
        Field nextField = getNextField(player, figureField);
        if (nextField == null) {
            return;
        }
        for (int i = 0; i < roll - 1; i++) {
            nextField = getNextField(player, nextField);
            if (nextField == null) {
                break;
            }
        }

        if (nextField == null) {
            return;
        }

        if (!nextField.isEmpty() && nextField.getPlacedFigure().color() == player.color()) {
            return;
        }

        result.add(new Action(figureField.getPlacedFigure(), figureField, nextField, roll));
    }

    private Field getNextField(Player player, Field figureField) {
        return figureField instanceof ExitField exitField && exitField.getColor() == player.color() ? exitField.getHomeField() : figureField.getNextField();
    }

    private Field getDestination(Field entranceField) {
        Field destinationField = entranceField.getNextField();
        for (int i = 0; i < 5; i++) {
            destinationField = destinationField.getNextField();
        }
        return destinationField;
    }

    private List<Field> getFigureFields(Player player) {
        return fields.stream()
                .filter(field -> field.getPlacedFigure() != null && field.getPlacedFigure().color() == player.color())
                .toList();
    }

    private boolean isFigureInBase(Player player, List<Field> figurePositions) {
        return figurePositions.stream()
                .anyMatch(field -> field instanceof BaseField baseField && baseField.getColor() == player.color() && !baseField.isEmpty());
    }

    private void createActionForBaseFigure(int roll, List<Field> figurePositions, List<Action> result) {
        Field baseField = figurePositions.stream()
                .filter(field -> field instanceof BaseField && !field.isEmpty())
                .findFirst()
                .orElseThrow();
        result.add(new Action(baseField.getPlacedFigure(), baseField, baseField.getNextField(), roll));
    }

    private boolean isFigureInEntrance(Player player, List<Field> figurePositions) {
        return figurePositions.stream()
                .anyMatch(field -> field instanceof EntranceField entranceField && entranceField.getColor() == player.color() && !field.isEmpty());
    }

    private Field getEntrance(Color color) {
        return switch (color) {
            case BLUE -> getField(BLUE_ENTRANCE_ID);
            case GREEN -> getField(GREEN_ENTRANCE_ID);
            case RED -> getField(RED_ENTRANCE_ID);
            case YELLOW -> getField(YELLOW_ENTRANCE_ID);
        };
    }

    public Color nextColor(Color color) {
        return switch (color) {
            case RED -> Color.YELLOW;
            case BLUE -> Color.GREEN;
            case GREEN -> Color.RED;
            case YELLOW -> Color.BLUE;
        };
    }

    public void moveFigure(Action action) {
        if (action.target().isEmpty()) {
            action.target().setPlacedFigure(action.figure());
            action.start().removeFigure();
        } else {
            placeFigureInBase(action.target().getPlacedFigure());
            action.target().removeFigure();
            action.target().placeFigure(action.figure());
            action.start().removeFigure();
        }
    }

    private void placeFigureInBase(Figure figure) {
        for (Field field : fields) {
            if (field instanceof BaseField baseField && baseField.getColor() == figure.color() && field.isEmpty()) {
                field.placeFigure(figure);
            }
        }
    }
}
