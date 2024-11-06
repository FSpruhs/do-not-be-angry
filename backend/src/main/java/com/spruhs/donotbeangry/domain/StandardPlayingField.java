package com.spruhs.donotbeangry.domain;

import com.spruhs.donotbeangry.domain.field.*;
import com.spruhs.donotbeangry.domain.player.Player;
import com.spruhs.donotbeangry.domain.player.Players;

import java.util.*;

public class StandardPlayingField implements PlayingField {

    private final int BLUE_ENTRANCE_ID = 0;
    private final int BLUE_EXIT_ID = 39;
    private final int GREEN_ENTRANCE_ID = 10;
    private final int GREEN_EXIT_ID = 9;
    private final int RED_ENTRANCE_ID = 20;
    private final int RED_EXIT_ID = 19;
    private final int YELLOW_ENTRANCE_ID = 30;
    private final int YELLOW_EXIT_ID = 29;

    private final List<Field> fields;

    public StandardPlayingField(Players players) {
        fields = new ArrayList<>();
        int counter = 0;

        List<HomeField> redHomes = createHomeFields(Color.RED);
        List<HomeField> blueHomes = createHomeFields(Color.BLUE);
        List<HomeField> greenHomes = createHomeFields(Color.GREEN);
        List<HomeField> yellowHomes = createHomeFields(Color.YELLOW);

        counter = initBaseField(counter);

        counter = initHomeFields(counter, blueHomes);
        counter = initHomeFields(counter, greenHomes);
        counter = initHomeFields(counter, redHomes);
        counter = initHomeFields(counter, yellowHomes);

        setExitFieldHome(BLUE_EXIT_ID, blueHomes);
        setExitFieldHome(GREEN_EXIT_ID, greenHomes);
        setExitFieldHome(RED_EXIT_ID, redHomes);
        setExitFieldHome(YELLOW_EXIT_ID, yellowHomes);

        counter = initBaseFields(counter, getField(BLUE_ENTRANCE_ID));
        counter = initBaseFields(counter, getField(GREEN_ENTRANCE_ID));
        counter = initBaseFields(counter, getField(RED_ENTRANCE_ID));
        initBaseFields(counter, getField(YELLOW_ENTRANCE_ID));

        for (Player player : players.players()) {
            putFiguresOnField(player.color());
        }
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

    private void setExitFieldHome(int fieldId, List<HomeField> homeFields) {
        if (getField(fieldId) instanceof ExitField exitField) {
            exitField.setHomeField(homeFields.getFirst());
        } else {
            throw new IllegalStateException("Field with id " + fieldId + " is not an ExitField");
        }
    }

    private List<HomeField> createHomeFields(Color color) {
        List<HomeField> result = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            result.add(new HomeField(color));
        }
        return result;
    }

    private int initHomeFields(int counter, List<HomeField> homes) {
        for (int i = 0; i < homes.size(); i++) {
            HomeField currentField = homes.get(i);
            currentField.setId(counter++);
            fields.add(currentField);

            if (i < homes.size() - 1) {
                currentField.setNextField(homes.get(i + 1));
            }
        }
        return counter;
    }


    private int initBaseFields(int counter, Field field) {
        if (!(field instanceof EntranceField entranceField)) {
            throw new IllegalArgumentException("Field with id " + field.getId() + " is not an EntranceField");
        }
        for (int i = 0; i < 4; i++) {
            BaseField baseField = new BaseField(entranceField.getColor());
            baseField.setId(counter++);
            baseField.setNextField(entranceField);
            fields.add(baseField);
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
        Map<Color, Integer> colorMap = new EnumMap<>(Color.class);

        for (Color color : Color.values()) {
            colorMap.put(color, 0);
        }

        for (Field field : fields) {
            if (field instanceof HomeField homeField && !homeField.isEmpty()) {
                colorMap.put(homeField.getColor(), colorMap.get(homeField.getColor()) + 1);
            }
        }

        for (Map.Entry<Color, Integer> entry : colorMap.entrySet()) {
            if (entry.getValue() == 4) {
                return Optional.of(entry.getKey());
            }
        }

        return Optional.empty();
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
