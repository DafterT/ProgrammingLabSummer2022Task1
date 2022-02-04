// Поле 8х8 клеток
// Фигуры: Пешка, конь, слон, ладья, ферзь, король
// Требования:
// 1. Ровно один белый король, ровно один чёрный король
// 2. Не более восьми белых пешек, не более восьми чёрных пешек
// 3. Короли не могут находиться на соседних клетках
// Операции:
// 1. Конструктор (сразу же указывает положение белого и чёрного короля)    v
// 2. Очистить клетку   v
// 3. Поставить новую фигуру (кроме короля)   v
// 4. Переместить существующую фигуру на другую клетку (соблюдать правила ходов не надо)

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class ChessField {
    // Белые фигуры
    private int countWhitePawn = 0;
    private final List<ChessFigure> piecesWhite;
    // Черные фигуры
    private int countBlackPawn = 0;
    private final List<ChessFigure> piecesBlack;

    public ChessField(ChessFigure king1, ChessFigure king2) {
        if (king1.equals(king2)) throw new IllegalArgumentException("The same coordinates");
        if (king1.getColor() == king2.getColor()) throw new IllegalArgumentException("The same color of the kings");
        if (king1.getFigureType() != FigureType.KING || king1.getFigureType() != king2.getFigureType())
            throw new IllegalArgumentException("Figure is not the KING");
        piecesWhite = new ArrayList<>();
        piecesBlack = new ArrayList<>();
        if (king1.getColor() == 'w') {
            piecesWhite.add(king1);
            piecesBlack.add(king2);
        } else {
            piecesWhite.add(king2);
            piecesBlack.add(king1);
        }
        if (abs(piecesWhite.get(0).getX() - piecesBlack.get(0).getX()) <= 1 &&
                abs(piecesWhite.get(0).getY() - piecesBlack.get(0).getY()) <= 1) {
            throw new IllegalArgumentException("The Kings are too close");
        }
    }

    // Отчистка клетки, кроме короля
    public boolean clearCage(int x, int y) {
        for (int i = 1; i < piecesWhite.size(); i++)
            if (piecesWhite.get(i).getX() == x && piecesWhite.get(i).getY() == y) {
                if (piecesWhite.get(i).getFigureType() == FigureType.PAWN) countWhitePawn--;
                piecesWhite.remove(i);
                return true;
            }
        for (int i = 1; i < piecesBlack.size(); i++)
            if (piecesBlack.get(i).getX() == x && piecesBlack.get(i).getY() == y) {
                if (piecesBlack.get(i).getFigureType() == FigureType.PAWN) countBlackPawn--;
                piecesBlack.remove(i);
                return true;
            }
        return false;
    }

    // Вернет true, если клетка занята
    private boolean checkCage(int x, int y) {
        for (ChessFigure chessFigure : piecesWhite)
            if (chessFigure.getX() == x && chessFigure.getY() == y) {
                return true;
            }
        for (ChessFigure chessFigure : piecesBlack)
            if (chessFigure.getX() == x && chessFigure.getY() == y) {
                return true;
            }
        return false;
    }

    public boolean moveFigure(int xStart, int yStart, int xStop, int yStop) {
        if (xStart > 8 || yStart > 8 || xStart < 1 || yStart < 1 || xStop > 8 || yStop > 8 || xStop < 1 || yStop < 1)
            return false;
        // Фигура, которая ходит
        ChessFigure walking = null;
        // Фигура, на которую ходят
        ChessFigure target = null;
        for (ChessFigure figure : piecesWhite) {
            if (figure.getX() == xStart && figure.getY() == yStart) {
                walking = figure;
            }
            if (figure.getX() == xStop && figure.getY() == yStop) {
                target = figure;
            }
        }
        if (walking == null || target == null)
            for (ChessFigure figure : piecesBlack) {
                if (figure.getX() == xStart && figure.getY() == yStart) {
                    walking = figure;
                }
                if (figure.getX() == xStop && figure.getY() == yStop) {
                    target = figure;
                }
            }
        if (walking == null) return false;
        // Корректность перемещения, если там король
        if (walking.getFigureType() == FigureType.KING) {
            if (walking.getColor() == 'b' && abs(xStop - piecesWhite.get(0).getX()) <= 1 &&
                    abs(yStop - piecesWhite.get(0).getY()) <= 1) return false;
            if (walking.getColor() == 'w' && abs(xStop - piecesBlack.get(0).getX()) <= 1 &&
                    abs(yStop - piecesBlack.get(0).getY()) <= 1) return false;
        }
        if (target != null) {
            // Нельзя есть фигуру, того же цвета
            if (walking.getColor() == target.getColor()) return false;
            // Всегда должен быть король (из ограничений)
            if (target.getFigureType() == FigureType.KING) return false;
            if (target.getFigureType() == FigureType.PAWN)
                if (target.getColor() == 'b') countBlackPawn--;
                else countWhitePawn--;
            if (target.getColor() == 'b') piecesBlack.remove(target);
            else piecesWhite.remove(target);
        }
        walking.move(xStop, yStop);
        return true;
    }

    // Добавление новой фигуры
    public boolean addCage(ChessFigure figure) {
        if (figure.getFigureType() == FigureType.KING) return false;
        if (checkCage(figure.getX(), figure.getY())) return false;
        if (figure.getFigureType() == FigureType.PAWN)
            if (figure.getColor() == 'b') {
                if (countBlackPawn == 8) return false;
                countBlackPawn++;
            } else {
                if (countWhitePawn == 8) return false;
                countWhitePawn++;
            }
        if (figure.getColor() == 'b') piecesBlack.add(figure);
        else piecesWhite.add(figure);
        return true;
    }
}