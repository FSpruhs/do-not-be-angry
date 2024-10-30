package com.spruhs.donotbeangry.domain;

import com.spruhs.donotbeangry.domain.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayingFieldTest {

    @Test
    void getField_shouldGetFieldWithId() {
        PlayingField playingField = new PlayingField();
        for (int i = 0; i < 72; i++) {
            assertThat(playingField.getField(i).getId()).isEqualTo(i);
        }
    }

    @Test
    void getField_shouldThrowException_whenFieldNotExists() {
        PlayingField playingField = new PlayingField();
        assertThatThrownBy(() -> playingField.getField(73))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void putFigureOnField_shouldPutFigureOnField() {
        PlayingField playingField = new PlayingField();
        playingField.putFiguresOnField(Color.BLUE);
        playingField.putFiguresOnField(Color.GREEN);
        playingField.putFiguresOnField(Color.RED);
        playingField.putFiguresOnField(Color.YELLOW);

        assertThat(playingField.getField(57).getPlacedFigure().color()).isEqualTo(Color.BLUE);
        assertThat(playingField.getField(58).getPlacedFigure().color()).isEqualTo(Color.BLUE);
        assertThat(playingField.getField(59).getPlacedFigure().color()).isEqualTo(Color.BLUE);
        assertThat(playingField.getField(60).getPlacedFigure().color()).isEqualTo(Color.BLUE);

        assertThat(playingField.getField(61).getPlacedFigure().color()).isEqualTo(Color.GREEN);
        assertThat(playingField.getField(62).getPlacedFigure().color()).isEqualTo(Color.GREEN);
        assertThat(playingField.getField(63).getPlacedFigure().color()).isEqualTo(Color.GREEN);
        assertThat(playingField.getField(64).getPlacedFigure().color()).isEqualTo(Color.GREEN);

        assertThat(playingField.getField(65).getPlacedFigure().color()).isEqualTo(Color.RED);
        assertThat(playingField.getField(66).getPlacedFigure().color()).isEqualTo(Color.RED);
        assertThat(playingField.getField(67).getPlacedFigure().color()).isEqualTo(Color.RED);
        assertThat(playingField.getField(68).getPlacedFigure().color()).isEqualTo(Color.RED);

        assertThat(playingField.getField(69).getPlacedFigure().color()).isEqualTo(Color.YELLOW);
        assertThat(playingField.getField(70).getPlacedFigure().color()).isEqualTo(Color.YELLOW);
        assertThat(playingField.getField(71).getPlacedFigure().color()).isEqualTo(Color.YELLOW);
        assertThat(playingField.getField(72).getPlacedFigure().color()).isEqualTo(Color.YELLOW);
    }

    @Test
    void winner_shouldBeEmpty_whenNoFiguresInHome() {
        PlayingField playingField = new PlayingField();

        playingField.putFiguresOnField(Color.BLUE);

        assertThat(playingField.winner()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("winnerTrue")
    void winner_shouldBeColor_whenAllFiguresInHome(Color color, List<Integer> homeIds) {
        PlayingField playingField = new PlayingField();

        playingField.getField(homeIds.get(0)).placeFigure(new Figure(color));
        playingField.getField(homeIds.get(1)).placeFigure(new Figure(color));
        playingField.getField(homeIds.get(2)).placeFigure(new Figure(color));
        playingField.getField(homeIds.get(3)).placeFigure(new Figure(color));

        assertThat(playingField.winner()).contains(color);
    }

    private static Stream<Arguments> winnerTrue() {
        return Stream.of(
                Arguments.of(Color.BLUE, List.of(41, 42, 43, 44)),
                Arguments.of(Color.GREEN, List.of(45, 46, 47, 48)),
                Arguments.of(Color.RED, List.of(49, 50, 51, 52)),
                Arguments.of(Color.YELLOW, List.of(53, 54, 55, 56))
        );
    }

    @Test
    void winner_shouldEmpty_whenNotAllBlueFiguresInHome() {
        PlayingField playingField = new PlayingField();

        playingField.getField(39).placeFigure(new Figure(Color.BLUE));
        playingField.getField(41).placeFigure(new Figure(Color.BLUE));
        playingField.getField(42).placeFigure(new Figure(Color.BLUE));
        playingField.getField(43).placeFigure(new Figure(Color.BLUE));

        assertThat(playingField.winner()).isEmpty();
    }

    @Test
    void possibleActions_shouldReturnLeaveBase_whenRollSix() {
        PlayingField playingField = new PlayingField();

        Figure figure = new Figure(Color.BLUE);
        playingField.getField(57).placeFigure(figure);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 6)).isEqualTo(List.of(new Action(figure, playingField.getField(57), playingField.getField(0), 6)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void possibleActions_shouldNotReturnLeaveBase_whenRollNotSix(int roll) {
        PlayingField playingField = new PlayingField();

        Figure figure = new Figure(Color.BLUE);
        playingField.getField(57).placeFigure(figure);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), roll)).isEqualTo(List.of());
    }

    @Test
    void possibleActions_shouldRNotReturnLeaveBase_whenEntryIsBlocked() {
        PlayingField playingField = new PlayingField();

        Figure figure1 = new Figure(Color.BLUE);
        Figure figure2 = new Figure(Color.BLUE);
        playingField.getField(0).placeFigure(figure1);
        playingField.getField(57).placeFigure(figure2);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 6)).isEqualTo(List.of(new Action(figure1, playingField.getField(0), playingField.getField(6), 6)));
    }

    @Test
    void possibleActions_shouldReturnKickOut_whenFieldContainsOpponent() {
        PlayingField playingField = new PlayingField();

        Figure figure1 = new Figure(Color.BLUE);
        Figure figure2 = new Figure(Color.GREEN);
        playingField.getField(0).placeFigure(figure1);
        playingField.getField(6).placeFigure(figure2);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 6)).isEqualTo(List.of(new Action(figure1, playingField.getField(0), playingField.getField(6), 6)));
    }

    @Test
    void possibleActions_shouldReturnStepOver() {
        PlayingField playingField = new PlayingField();

        Figure figure1 = new Figure(Color.BLUE);
        Figure figure2 = new Figure(Color.BLUE);
        playingField.getField(0).placeFigure(figure1);
        playingField.getField(5).placeFigure(figure2);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 6)).containsExactlyInAnyOrder(new Action(figure1, playingField.getField(0), playingField.getField(6), 6), new Action(figure2, playingField.getField(5), playingField.getField(11), 6));
    }

    @Test
    void possibleActions_shouldReturnEnterHome() {
        PlayingField playingField = new PlayingField();

        Figure figure = new Figure(Color.BLUE);
        playingField.getField(38).placeFigure(figure);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 3)).isEqualTo(List.of(new Action(figure, playingField.getField(38), playingField.getField(41), 3)));
    }

    @Test
    void possibleActions_shouldReturnEnterHome_whenOnExitField() {
        PlayingField playingField = new PlayingField();

        Figure figure = new Figure(Color.BLUE);
        playingField.getField(40).placeFigure(figure);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 1)).isEqualTo(List.of(new Action(figure, playingField.getField(40), playingField.getField(41), 1)));
    }

    @Test
    void possibleActions_shouldReturnCircle_whenStepOverEnd() {
        PlayingField playingField = new PlayingField();

        Figure figure = new Figure(Color.GREEN);
        playingField.getField(39).placeFigure(figure);

        assertThat(playingField.possibleActions(new Player(Color.GREEN), 3)).isEqualTo(List.of(new Action(figure, playingField.getField(39), playingField.getField(1), 3)));
    }


    @Test
    void possibleActions_shouldNotEnterOpponentsHome() {
        PlayingField playingField = new PlayingField();

        Figure figure = new Figure(Color.BLUE);
        playingField.getField(9).placeFigure(figure);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 3)).isEqualTo(List.of(new Action(figure, playingField.getField(9), playingField.getField(12), 3)));
    }

    @Test
    void possibleActions_shouldReturnEmpty_whenHomeEnds() {
        PlayingField playingField = new PlayingField();

        Figure figure = new Figure(Color.BLUE);
        playingField.getField(41).placeFigure(figure);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 6)).isEqualTo(List.of());
    }

    @Test
    void possibleActions_shouldReturnEmpty_whenHomeIsBlocked() {
        PlayingField playingField = new PlayingField();

        Figure figure1 = new Figure(Color.BLUE);
        Figure figure2 = new Figure(Color.BLUE);
        playingField.getField(40).placeFigure(figure1);
        playingField.getField(42).placeFigure(figure2);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 3)).isEqualTo(List.of());
    }

    @Test
    void possibleActions_shouldReturnOneAction_whenSelfBlocked() {
        PlayingField playingField = new PlayingField();

        Figure figure1 = new Figure(Color.BLUE);
        Figure figure2 = new Figure(Color.BLUE);
        playingField.getField(1).placeFigure(figure1);
        playingField.getField(2).placeFigure(figure2);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 1)).isEqualTo(List.of(new Action(figure1, playingField.getField(2), playingField.getField(3), 1)));
    }

    @Test
    void possibleActions_shouldOnlyReturnEntryMove_whenFigureInBase() {
        PlayingField playingField = new PlayingField();

        Figure figure1 = new Figure(Color.BLUE);
        Figure figure2 = new Figure(Color.BLUE);
        Figure figure3 = new Figure(Color.BLUE);

        playingField.getField(0).placeFigure(figure1);
        playingField.getField(57).placeFigure(figure2);
        playingField.getField(2).placeFigure(figure3);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 6)).isEqualTo(List.of(new Action(figure1, playingField.getField(0), playingField.getField(6), 6)));

    }

    @Test
    void possibleActions_shouldReturnMultipleMoves_whenNoFigureInBase() {
        PlayingField playingField = new PlayingField();

        Figure figure1 = new Figure(Color.BLUE);
        Figure figure2 = new Figure(Color.BLUE);

        playingField.getField(0).placeFigure(figure1);
        playingField.getField(2).placeFigure(figure2);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 6)).isEqualTo(List.of(
                new Action(figure1, playingField.getField(0), playingField.getField(6), 6),
                new Action(figure1, playingField.getField(2), playingField.getField(8), 6)
        ));
    }

    @Test
    void possibleActions_shouldOnlyLeaveBase_whenRollSix() {
        PlayingField playingField = new PlayingField();

        Figure figure1 = new Figure(Color.BLUE);
        Figure figure2 = new Figure(Color.BLUE);

        playingField.getField(57).placeFigure(figure1);
        playingField.getField(2).placeFigure(figure2);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 6)).isEqualTo(List.of(new Action(figure1, playingField.getField(57), playingField.getField(0), 6)));

    }

    @Test
    void possibleActions_shouldReturnMultipleMoves_whenRollSixAndBaseIsEmpty() {
        PlayingField playingField = new PlayingField();

        Figure figure1 = new Figure(Color.BLUE);
        Figure figure2 = new Figure(Color.BLUE);

        playingField.getField(1).placeFigure(figure1);
        playingField.getField(2).placeFigure(figure2);

        assertThat(playingField.possibleActions(new Player(Color.BLUE), 6)).isEqualTo(List.of(
                new Action(figure1, playingField.getField(1), playingField.getField(7), 6),
                new Action(figure1, playingField.getField(2), playingField.getField(8), 6)
        ));
    }
}