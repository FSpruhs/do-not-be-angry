package com.spruhs.donotbeangry.domain;

import com.spruhs.donotbeangry.domain.field.*;
import com.spruhs.donotbeangry.domain.player.Player;

import java.util.*;

public class StandardPlayingField implements PlayingField {

    private final List<Field> fields;

    public StandardPlayingField() {
        EntranceField redEntrance = new EntranceField(Color.RED);
        EntranceField blueEntrance = new EntranceField(Color.BLUE);
        EntranceField greenEntrance = new EntranceField(Color.GREEN);
        EntranceField yellowEntrance = new EntranceField(Color.YELLOW);

        ExitField redExit = new ExitField(Color.RED);
        ExitField blueExit = new ExitField(Color.BLUE);
        ExitField greenExit = new ExitField(Color.GREEN);
        ExitField yellowExit = new ExitField(Color.YELLOW);

        List<HomeField> redHomes = List.of(new HomeField(Color.RED), new HomeField(Color.RED), new HomeField(Color.RED), new HomeField(Color.RED));
        List<HomeField> blueHomes = List.of(new HomeField(Color.BLUE), new HomeField(Color.BLUE), new HomeField(Color.BLUE), new HomeField(Color.BLUE));
        List<HomeField> greenHomes = List.of(new HomeField(Color.GREEN), new HomeField(Color.GREEN), new HomeField(Color.GREEN), new HomeField(Color.GREEN));
        List<HomeField> yellowHomes = List.of(new HomeField(Color.YELLOW), new HomeField(Color.YELLOW), new HomeField(Color.YELLOW), new HomeField(Color.YELLOW));

        fields = new ArrayList<>();

        int counter = 0;

        Field actualField = blueEntrance;
        for (int i = 0; i < 41; i++) {

            Field nextField = switch (i) {
                case 9 -> greenExit;
                case 10 -> greenEntrance;
                case 19 -> redExit;
                case 20 -> redEntrance;
                case 29 -> yellowExit;
                case 30 -> yellowEntrance;
                case 39 -> blueExit;
                default -> new StandardField();
            };

            actualField.setNextField(nextField);
            actualField.setId(counter);
            counter++;
            fields.add(actualField);
            actualField = nextField;
        }
        blueExit.setNextField(blueEntrance);

        HomeField actualHomeField = blueHomes.get(0);
        for (int i = 0; i < 4; i++) {
            actualHomeField.setId(counter);
            counter++;
            fields.add(actualHomeField);
            if (i != 3) {
                actualHomeField.setNextField(blueHomes.get(i + 1));
                actualHomeField = blueHomes.get(i + 1);
            }
        }

        actualHomeField = greenHomes.get(0);
        for (int i = 0; i < 4; i++) {
            actualHomeField.setId(counter);
            counter++;
            fields.add(actualHomeField);
            if (i != 3) {
                actualHomeField.setNextField(greenHomes.get(i + 1));
                actualHomeField = greenHomes.get(i + 1);
            }
                    }

        actualHomeField = redHomes.get(0);
        for (int i = 0; i < 4; i++) {
            actualHomeField.setId(counter);
            counter++;
            fields.add(actualHomeField);
            if (i != 3) {
                actualHomeField.setNextField(redHomes.get(i + 1));
                actualHomeField = redHomes.get(i + 1);
            }
        }

        actualHomeField = yellowHomes.get(0);
        for (int i = 0; i < 4; i++) {
            actualHomeField.setId(counter);
            counter++;
            fields.add(actualHomeField);
            if (i != 3) {
                actualHomeField.setNextField(yellowHomes.get(i + 1));
                actualHomeField = yellowHomes.get(i + 1);
            }

        }

        blueExit.setHomeField(blueHomes.get(0));
        greenExit.setHomeField(greenHomes.get(0));
        redExit.setHomeField(redHomes.get(0));
        yellowExit.setHomeField(yellowHomes.get(0));

        for (int i = 0; i < 4; i++) {
            BaseField baseField = new BaseField(Color.BLUE);
            baseField.setId(counter);
            counter++;
            baseField.setNextField(blueEntrance);
            fields.add(baseField);
        }

        for (int i = 0; i < 4; i++) {
            BaseField baseField = new BaseField(Color.GREEN);
            baseField.setId(counter);
            counter++;
            baseField.setNextField(greenEntrance);
            fields.add(baseField);
        }

        for (int i = 0; i < 4; i++) {
            BaseField baseField = new BaseField(Color.RED);
            baseField.setId(counter);
            counter++;
            baseField.setNextField(redEntrance);
            fields.add(baseField);
        }

        for (int i = 0; i < 4; i++) {
            BaseField baseField = new BaseField(Color.YELLOW);
            baseField.setId(counter);
            counter++;
            baseField.setNextField(yellowEntrance);
            fields.add(baseField);
        }
    }

    public Field getField(int id) {
        if (id < 0 || id >= fields.size()) {
            throw new IllegalArgumentException("Field with id " + id + " does not exist");
        }
        return fields.get(id);
    }

    public void putFiguresOnField(Color color) {
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
                for (int i = 0; i < roll - 1; i++) {
                    nextField = nextField instanceof ExitField exitField && exitField.getColor() == player.color()  ? exitField.getHomeField() : nextField.getNextField();;
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
