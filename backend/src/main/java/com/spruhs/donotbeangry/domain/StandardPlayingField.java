package com.spruhs.donotbeangry.domain;

import com.spruhs.donotbeangry.domain.field.*;
import com.spruhs.donotbeangry.domain.player.Player;
import com.spruhs.donotbeangry.domain.player.Players;

import java.util.*;

public class StandardPlayingField implements PlayingField {

    private final List<Field> fields;

    public StandardPlayingField(Players players) {
        fields = new ArrayList<>();
        int counter = 0;

        EntranceField redEntrance = new EntranceField(Color.RED);
        EntranceField blueEntrance = new EntranceField(Color.BLUE);
        EntranceField greenEntrance = new EntranceField(Color.GREEN);
        EntranceField yellowEntrance = new EntranceField(Color.YELLOW);

        ExitField redExit = new ExitField(Color.RED);
        ExitField blueExit = new ExitField(Color.BLUE);
        ExitField greenExit = new ExitField(Color.GREEN);
        ExitField yellowExit = new ExitField(Color.YELLOW);

        List<HomeField> redHomes = createHomeFields(Color.RED);
        List<HomeField> blueHomes = createHomeFields(Color.BLUE);
        List<HomeField> greenHomes = createHomeFields(Color.GREEN);
        List<HomeField> yellowHomes = createHomeFields(Color.YELLOW);

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
            actualField.setId(counter++);
            fields.add(actualField);
            actualField = nextField;
        }
        blueExit.setNextField(blueEntrance);

        counter = initHomeFields(counter, blueHomes);
        counter = initHomeFields(counter, greenHomes);
        counter = initHomeFields(counter, redHomes);
        counter = initHomeFields(counter, yellowHomes);

        blueExit.setHomeField(blueHomes.getFirst());
        greenExit.setHomeField(greenHomes.getFirst());
        redExit.setHomeField(redHomes.getFirst());
        yellowExit.setHomeField(yellowHomes.getFirst());

        counter = initBaseFields(counter, blueEntrance);
        counter = initBaseFields(counter, greenEntrance);
        counter = initBaseFields(counter, redEntrance);
        initBaseFields(counter, yellowEntrance);

        for (Player player : players.players()) {
            putFiguresOnField(player.color());
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


    private int initBaseFields(int counter, EntranceField entranceField) {
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
