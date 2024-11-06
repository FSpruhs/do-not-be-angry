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

        if (roll == 6) {
            for (Field field : fields) {
                if (field instanceof BaseField baseField && baseField.getColor() == player.color() && !baseField.isEmpty()) {
                    for (Field field1 : fields) {
                        if (field1 instanceof EntranceField entranceField && entranceField.getColor() == player.color() && (field1.isEmpty() || !field1.isEmpty() && field1.getPlacedFigure().color() != player.color())) {
                            result.add(new Action(field.getPlacedFigure(), field, field1, roll));
                            return result;
                        }
                    }
                }
            }
        }

        for (Field field: fields) {
            if (field instanceof EntranceField entranceField && entranceField.getColor() == player.color() && !field.isEmpty() && field.getPlacedFigure().color() == player.color()) {
                for (Field field1 : fields) {
                    if (field1 instanceof BaseField baseField && baseField.getColor() == player.color() && !baseField.isEmpty()) {
                        if (getField(field.getId() + roll).isEmpty() || !getField(field.getId() + roll).isEmpty() && getField(field.getId() + roll).getPlacedFigure().color() != player.color()) {
                            result.add(new Action(field.getPlacedFigure(), field, getField(field.getId() + roll), roll));
                            return result;
                        }
                    }
                }
            }
        }
        for (Field field : fields) {
            if (field.getPlacedFigure() != null && field.getPlacedFigure().color() == player.color()) {
                if (field instanceof BaseField baseField) {
                    if (roll == 6 && baseField.getNextField().isEmpty() || baseField.getPlacedFigure().color() != player.color()) {
                        result.add(new Action(field.getPlacedFigure(), field, baseField.getNextField(), roll));
                    }
                    continue;
                }

                Field nextField = field instanceof ExitField exitField && exitField.getColor() == player.color() ? exitField.getHomeField() : field.getNextField();
                if (nextField == null) {
                    continue;
                }
                for (int i = 0; i < roll - 1; i++) {
                    nextField = nextField instanceof ExitField exitField && exitField.getColor() == player.color() ? exitField.getHomeField() : nextField.getNextField();
                    if (nextField == null) {
                        break;
                    }
                }

                if (nextField == null) {
                    continue;
                }

                if (!nextField.isEmpty() && nextField.getPlacedFigure().color() == player.color()) {
                    continue;
                }

                result.add(new Action(field.getPlacedFigure(), field, nextField, roll));
            }
        }
        return result;
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
