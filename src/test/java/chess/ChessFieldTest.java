package chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessFieldTest {
    @Test
    void constructorTest() {
        // Проверка, что создалось без прошествий
        ChessField field = new ChessField(new ChessFigure(1, 1, FigureType.KING, ChessFigure.Color.WHITE),
                new ChessFigure(1, 3, FigureType.KING, ChessFigure.Color.BLACK));
        ChessField field2 = new ChessField(new ChessFigure(1, 1, FigureType.KING, ChessFigure.Color.BLACK),
                new ChessFigure(1, 3, FigureType.KING, ChessFigure.Color.WHITE));
        // Одинаковые координаты королей
        assertThrows(IllegalArgumentException.class, () -> {
            ChessField chessField = new ChessField(new ChessFigure(1, 1, FigureType.KING, ChessFigure.Color.WHITE),
                    new ChessFigure(1, 1, FigureType.KING, ChessFigure.Color.BLACK));
        });
        // Одинаковые цвета королей
        assertThrows(IllegalArgumentException.class, () -> {
            ChessField chessField = new ChessField(new ChessFigure(1, 1, FigureType.KING, ChessFigure.Color.WHITE),
                    new ChessFigure(1, 3, FigureType.KING, ChessFigure.Color.WHITE));
        });
        // Короли слишком близко
        assertThrows(IllegalArgumentException.class, () -> {
            ChessField chessField = new ChessField(new ChessFigure(1, 1, FigureType.KING, ChessFigure.Color.WHITE),
                    new ChessFigure(1, 2, FigureType.KING, ChessFigure.Color.BLACK));
        });
        // Фигура не является королем
        assertThrows(IllegalArgumentException.class, () -> {
            ChessField chessField = new ChessField(new ChessFigure(1, 1, FigureType.KING, ChessFigure.Color.WHITE),
                    new ChessFigure(1, 3, FigureType.BISHOP, ChessFigure.Color.BLACK));
        });
    }

    @Test
    void addAndClearTest() {
        ChessField field = new ChessField(new ChessFigure(1, 1, FigureType.KING, ChessFigure.Color.WHITE),
                new ChessFigure(1, 8, FigureType.KING, ChessFigure.Color.BLACK));
        assertFalse(field.addSquare(new ChessFigure(1, 5, FigureType.KING, ChessFigure.Color.BLACK)));
        assertFalse(field.addSquare(new ChessFigure(1, 8, FigureType.BISHOP, ChessFigure.Color.BLACK)));
        for (int i = 1; i <= 8; i++) {
            assertTrue(field.addSquare(new ChessFigure(2, i, FigureType.PAWN, ChessFigure.Color.BLACK)));
        }
        assertFalse(field.addSquare(new ChessFigure(3, 8, FigureType.PAWN, ChessFigure.Color.BLACK)));
        assertTrue(field.clearSquare(2, 1));
        assertTrue(field.addSquare(new ChessFigure(3, 8, FigureType.PAWN, ChessFigure.Color.BLACK)));
        assertFalse(field.clearSquare(1, 1));
        assertFalse(field.addSquare(null));
    }

    @Test
    void moveFigure() {
        ChessField field = new ChessField(new ChessFigure(1, 1, FigureType.KING, ChessFigure.Color.WHITE),
                new ChessFigure(1, 8, FigureType.KING, ChessFigure.Color.BLACK));
        assertTrue(field.moveFigure(1, 8, 1, 7));
        assertFalse(field.moveFigure(1, 7, 1, 2));
        assertFalse(field.moveFigure(1, 7, 1, 1));
        assertFalse(field.moveFigure(9, 9, 1, 1));
        assertTrue(field.moveFigure(1, 7, 8, 8));
        assertTrue(field.addSquare(new ChessFigure(1, 2, FigureType.PAWN, ChessFigure.Color.WHITE)));
        assertFalse(field.moveFigure(8, 8, 1, 2));
        assertTrue(field.moveFigure(1, 2, 2, 3));
        assertTrue(field.moveFigure(8, 8, 2, 3));
        // Проверка, что counter пешек уменьшился
        for (int i = 1; i <= 8; i++) {
            assertTrue(field.addSquare(new ChessFigure(3, i, FigureType.PAWN, ChessFigure.Color.WHITE)));
        }
        assertTrue(field.addSquare(new ChessFigure(1, 5, FigureType.BISHOP, ChessFigure.Color.BLACK)));
        assertTrue(field.moveFigure(3, 3, 1, 5));
        assertFalse(field.moveFigure(1, 1, 2, 2));
    }

    @Test
    void checkKing() {
        ChessField field = new ChessField(new ChessFigure(1, 1, FigureType.KING, ChessFigure.Color.WHITE),
                new ChessFigure(1, 8, FigureType.KING, ChessFigure.Color.BLACK));
        // Тест, что они не угрожают друг-другу
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        // Тест с безобидной пешкой
        assertTrue(field.addSquare(new ChessFigure(1, 2, FigureType.PAWN, ChessFigure.Color.WHITE)));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        // Угроза белому королю
        assertTrue(field.addSquare(new ChessFigure(6, 6, FigureType.BISHOP, ChessFigure.Color.BLACK)));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        // Спасём его чёрным конем
        assertTrue(field.addSquare(new ChessFigure(2, 2, FigureType.KNIGHT, ChessFigure.Color.BLACK)));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        // Переместим коня ЗА слона, чтоб он не закрывал короля
        assertTrue(field.moveFigure(2, 2, 7, 7));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        // Создадим новую угрозу белому королю, закрыв старую
        assertTrue(field.addSquare(new ChessFigure(5, 5, FigureType.BISHOP, ChessFigure.Color.BLACK)));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        // Белая ладья нападает на чёрного короля
        assertTrue(field.addSquare(new ChessFigure(8, 8, FigureType.ROOK, ChessFigure.Color.WHITE)));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        // Переместим ладью левее и создадим фигуру за ней
        assertTrue(field.addSquare(new ChessFigure(7, 8, FigureType.PAWN, ChessFigure.Color.WHITE)));
        assertTrue(field.moveFigure(8, 8, 5, 8));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        // Закроем ладью пешкой
        assertTrue(field.addSquare(new ChessFigure(3, 8, FigureType.PAWN, ChessFigure.Color.WHITE)));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        // Была ошибка, еще один тест с ладьей, теперь по оси Y
        assertTrue(field.addSquare(new ChessFigure(1, 3, FigureType.ROOK, ChessFigure.Color.WHITE)));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.addSquare(new ChessFigure(1, 4, FigureType.PAWN, ChessFigure.Color.WHITE)));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        // Тест пешки, не сможет съесть т.к. она может ходить только вверх (белая)
        assertTrue(field.addSquare(new ChessFigure(2, 7, FigureType.PAWN, ChessFigure.Color.WHITE)));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        // Тест пешки. Переместим короля, чтоб пешка могла съесть его
        assertTrue(field.moveFigure(1, 8, 1, 6));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        // Удалим угрозы белому королю и тест, не ест ли пешка весь ряд
        assertTrue(field.clearSquare(5, 5)); // Не имело смысла т.к. я в любом случае переместил короля
        assertTrue(field.clearSquare(6, 6)); // Но пусть будет в качестве теста
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.moveFigure(1, 1, 3, 2));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.addSquare(new ChessFigure(5, 1, FigureType.PAWN, ChessFigure.Color.BLACK)));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.moveFigure(5, 1, 4, 1));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        // Тест, что пешка вперед не ест
        assertTrue(field.moveFigure(4, 1, 3, 1));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        // Проверка второй диагонали у слона
        assertTrue(field.clearSquare(2, 7));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.moveFigure(1, 6, 1, 8));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.addSquare(new ChessFigure(6, 3, FigureType.BISHOP, ChessFigure.Color.WHITE)));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        // Добавим в защиту черную пешку
        assertTrue(field.addSquare(new ChessFigure(2, 7, FigureType.PAWN, ChessFigure.Color.BLACK)));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        // Коня проверять смысла нет т.к. там только арифметика, которую я проверил руками
        // Королеву проверять смысла нет т.к. там просто ctrl+c, ctrl+v из ладьи и слона
        // Однако на всякий случай я проведу пару тестов с этими фигурами
        assertTrue(field.addSquare(new ChessFigure(2, 6, FigureType.KNIGHT, ChessFigure.Color.WHITE)));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.clearSquare(2, 6));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.addSquare(new ChessFigure(1, 5, FigureType.QUEEN, ChessFigure.Color.WHITE)));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertTrue(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.moveFigure(1, 5, 3, 5));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.addSquare(new ChessFigure(7, 2, FigureType.QUEEN, ChessFigure.Color.BLACK)));
        assertTrue(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
        assertTrue(field.addSquare(new ChessFigure(5, 2, FigureType.PAWN, ChessFigure.Color.BLACK)));
        assertFalse(field.checkKing(ChessFigure.Color.WHITE));
        assertFalse(field.checkKing(ChessFigure.Color.BLACK));
    }
}