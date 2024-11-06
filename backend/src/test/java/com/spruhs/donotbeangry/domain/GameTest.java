package com.spruhs.donotbeangry.domain;

import com.spruhs.donotbeangry.domain.field.Field;
import com.spruhs.donotbeangry.domain.field.StandardField;
import com.spruhs.donotbeangry.domain.player.Player;
import com.spruhs.donotbeangry.domain.player.Players;
import com.spruhs.donotbeangry.domain.player.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameTest {

    @Mock
    private Dice diceMock;

    @Mock
    private PlayingField playingFieldMock;

    @Mock
    private Players playersMock;

    @InjectMocks
    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game(playersMock, playingFieldMock, diceMock);
    }

    @Test
    void start_playerShouldRollThreeTimes_whenHasNoActions() {
        when(diceMock.roll()).thenReturn(1);
        when(playingFieldMock.winner()).thenReturn(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Color.RED));
        when(playersMock.players()).thenReturn(List.of(new Player(Color.RED, new Random()), new Player(Color.BLUE, new Random())));
        when(playingFieldMock.possibleActions(any(), anyInt())).thenReturn(List.of());
        when(playingFieldMock.nextColor(any())).thenReturn(Color.RED);
        when(playersMock.containsColor(any())).thenReturn(true);
        when(playersMock.getPlayerByColor(any())).thenReturn(new Player(Color.RED, new Random()));

        game.start();

        InOrder inOrder = inOrder(playingFieldMock);

        inOrder.verify(playingFieldMock, times(3)).possibleActions(new Player(Color.BLUE, new Random()), 1);
        inOrder.verify(playingFieldMock, times(1)).possibleActions(new Player(Color.RED, new Random()), 1);
    }

    @Test
    void start_playerShouldNotRollThreeTimes_whenNoActionAfterSixRoll() {
        when(diceMock.roll()).thenReturn(1, 6, 1, 1);
        when(playingFieldMock.winner()).thenReturn(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Color.RED));
        when(playersMock.players()).thenReturn(List.of(new Player(Color.RED, new Random()), new Player(Color.BLUE, new Random())));

        when(playingFieldMock.possibleActions(any(), anyInt())).thenReturn(List.of(new Action(new Figure(Color.BLUE), new StandardField(), new StandardField(), 6)), List.of());
        when(playingFieldMock.nextColor(any())).thenReturn(Color.RED);
        when(playersMock.containsColor(any())).thenReturn(true);
        when(playersMock.getPlayerByColor(any())).thenReturn(new Player(Color.RED, new Random()));

        game.start();

        InOrder inOrder = inOrder(playingFieldMock);

        inOrder.verify(playingFieldMock, times(1)).possibleActions(new Player(Color.BLUE, new Random()), 6);
        inOrder.verify(playingFieldMock, times(1)).possibleActions(new Player(Color.BLUE, new Random()), 1);
        inOrder.verify(playingFieldMock, times(1)).possibleActions(new Player(Color.RED, new Random()), 1);
    }

    @Test
    void start_playerShouldTakeOneRoll_whenRollIsNotSix() {
        when(diceMock.roll()).thenReturn(1, 5, 1);
        when(playingFieldMock.winner()).thenReturn(Optional.empty(), Optional.empty(), Optional.of(Color.RED));
        when(playersMock.players()).thenReturn(List.of(new Player(Color.RED, new Random()), new Player(Color.BLUE, new Random())));

        when(playingFieldMock.possibleActions(any(), anyInt())).thenReturn(List.of(new Action(new Figure(Color.BLUE), new StandardField(), new StandardField(), 6)), List.of());
        when(playingFieldMock.nextColor(any())).thenReturn(Color.RED);
        when(playersMock.containsColor(any())).thenReturn(true);
        when(playersMock.getPlayerByColor(any())).thenReturn(new Player(Color.RED, new Random()));

        game.start();

        InOrder inOrder = inOrder(playingFieldMock);

        inOrder.verify(playingFieldMock, times(1)).possibleActions(new Player(Color.BLUE, new Random()), 5);
        inOrder.verify(playingFieldMock, times(1)).possibleActions(new Player(Color.RED, new Random()), 1);
    }

    @Test
    void start_playerShouldTakeOneRoll_whenRollIsNotSixAndNextColorIsNull() {
        when(diceMock.roll()).thenReturn(1, 5, 1);
        when(playingFieldMock.winner()).thenReturn(Optional.empty(), Optional.empty(), Optional.of(Color.RED));
        when(playersMock.players()).thenReturn(List.of(new Player(Color.RED, new Random()), new Player(Color.BLUE, new Random())));

        when(playingFieldMock.possibleActions(any(), anyInt())).thenReturn(List.of(new Action(new Figure(Color.BLUE), new StandardField(), new StandardField(), 6)), List.of());
        when(playingFieldMock.nextColor(any())).thenReturn(Color.GREEN, Color.RED);
        when(playersMock.containsColor(Color.GREEN)).thenReturn(false);
        when(playersMock.containsColor(Color.RED)).thenReturn(true);
        when(playersMock.getPlayerByColor(any())).thenReturn(new Player(Color.RED, new Random()));

        game.start();

        InOrder inOrder = inOrder(playingFieldMock);

        inOrder.verify(playingFieldMock, times(1)).possibleActions(new Player(Color.BLUE, new Random()), 5);
        inOrder.verify(playingFieldMock, times(1)).possibleActions(new Player(Color.RED, new Random()), 1);
    }



}