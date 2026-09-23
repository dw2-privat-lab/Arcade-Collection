package chess;

import java.awt.*;
import java.util.ArrayList;

public class Schachlogik {
    public boolean whiteMoves;
    public int oldY, oldToX, oldToY;
    public boolean chooseNewPiece;
    public boolean checkmate;
    public boolean stalemate;
    /**
     * 0=NIX
     * 1=Bauer
     * 2=Läufer
     * 3=Springer
     * 4=Turm
     * 5=Dame
     * 6=König
     * +=weiß
     * -=schwarz
     */
    int[][] Schachfeld;
    int[][] UsedTiles;

    public Schachlogik() {
        reset();
    }

    public void reset() {
        Schachfeld = new int[][]{
                {-4, -3, -2, -5, -6, -2, -3, -4},
                {-1, -1, -1, -1, -1, -1, -1, -1},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1},
                {4, 3, 2, 5, 6, 2, 3, 4},
        };
        UsedTiles = new int[8][8];
        whiteMoves = true;
        chooseNewPiece = false;
        checkmate = false;
        stalemate = false;
        oldY = 0;
        oldToX = 0;
        oldToY = 0;
    }

    public void move(int x, int y, int toX, int toY) {
        if (canMove(x, y, toX, toY)) {
            int tempOrigin = Schachfeld[y][x];

            //En Passant Bauer löschen
            if (Math.abs(tempOrigin) == 1 && x != toX && Schachfeld[toY][toX] == 0) {
                if (toX == oldToX && oldToY == y) {
                    Schachfeld[oldToY][oldToX] = 0;
                }
            }
            int colormodifier = tempOrigin > 0 ? 1 : -1;
            if (validRochade(x, y, toX, toY)) {
                if (toX == 0 || toX == 2) {
                    Schachfeld[toY][2] = 6 * colormodifier;
                    Schachfeld[toY][3] = 4 * colormodifier;
                    Schachfeld[toY][0] = 0;
                } else if (toX == 7 || toX == 6) {
                    Schachfeld[toY][6] = 6 * colormodifier;
                    Schachfeld[toY][5] = 4 * colormodifier;
                    Schachfeld[toY][7] = 0;
                }
            } else {
                Schachfeld[toY][toX] = tempOrigin;
            }
            Schachfeld[y][x] = 0;

            UsedTiles[y][x] = 1;
            UsedTiles[toY][toX] = 1;

            if (Math.abs(tempOrigin) == 1 && ((tempOrigin > 0 && toY == 0) || (tempOrigin < 0 && toY == 7))){
                chooseNewPiece = true;
        }


            oldY = y;
            oldToX = toX;
            oldToY = toY;
            if (checkCheckmate(whiteMoves ? -1:1)) {
                checkmate = true;
            } else {
                if(!chooseNewPiece) {
                    whiteMoves = !whiteMoves;
                    if (isStalemate(whiteMoves ? 1 : -1))
                        stalemate = true;
                }
            }
        }
    }

    public boolean canMove(int x, int y, int toX, int toY) {
        if (!istAufFeld(x, y, toX, toY)) return false;
        if (!istAnDerReihe(x, y)) return false;
        if (x == toX && y == toY) return false;
        if (validRochade(x, y, toX, toY)) return true;
        if (Schachfeld[toY][toX] * Schachfeld[y][x] > 0) return false;
        if (!isValidPieceMove(x, y, toX, toY)) return false;

        return blocksCheck(x, y, toX, toY);
    }

    private boolean isValidPieceMove(int x, int y, int toX, int toY) {
        int Piece = Schachfeld[y][x];
        switch (Piece) {
            case 1, -1:
                // 1 nach Vorne
                if (((y - toY) * Piece == 1) && (x == toX) && (Schachfeld[toY][toX] == 0))
                    return true;

                // 2 nach Vorne
                if (((Piece > 0 && y == 6) || (Piece < 0 && y == 1)) && ((y - toY) * Piece == 2) && x == toX && Schachfeld[toY + Piece][toX] == 0 && Schachfeld[toY][toX] == 0)
                    return true;

                // 1 Diagonal nach Vorne (Normales Schlagen)
                if ((Schachfeld[toY][toX] * Piece < 0) && ((y - toY) * Piece == 1) && (Math.abs(x - toX) == 1))
                    return true;

                // En Passant
                boolean isOpponentPawnLastMove = (Schachfeld[oldToY][oldToX] == -Piece);
                boolean lastMoveWasTwoSquares = Math.abs(oldY - oldToY) == 2;
                boolean isAdjacentColumn = Math.abs(x - oldToX) == 1 && y == oldToY;
                boolean isMovingBehindEnemyPawn = (toX == oldToX) && (toY == y - Piece);

                if (isOpponentPawnLastMove && lastMoveWasTwoSquares && isAdjacentColumn && isMovingBehindEnemyPawn && Schachfeld[toY][toX] == 0) {
                    return true;
                }
                break;

            case 2, -2:
                if (Math.abs(x - toX) == Math.abs(toY - y))
                    return istWegFrei(x, y, toX, toY);
                break;

            case 3, -3:
                return Math.abs(x - toX) * Math.abs(y - toY) == 2;

            case 4, -4:
                if (x == toX || y == toY)
                    return istWegFrei(x, y, toX, toY);
                break;

            case 5, -5:
                if ((x == toX || y == toY) || (Math.abs(x - toX) == Math.abs(toY - y)))
                    return istWegFrei(x, y, toX, toY);
                break;

            case 6, -6:
                if (Math.abs(x - toX) <= 1 && Math.abs(y - toY) <= 1)
                    return true;
        }
        return false;
    }

    private boolean istAnDerReihe(int x, int y) {
        return whiteMoves && Schachfeld[y][x] > 0 || !whiteMoves && Schachfeld[y][x] < 0;
    }

    private boolean istAufFeld(int x, int y, int toX, int toY) {
        return x <= 7 && x >= 0 && y <= 7 && y >= 0 && toX <= 7 && toX >= 0 && toY <= 7 && toY >= 0;
    }

    private boolean istWegFrei(int x, int y, int toX, int toY) {
        int dx = Integer.compare(toX, x);
        int dy = Integer.compare(toY, y);
        int distanz = Math.max(Math.abs(toX - x), Math.abs(toY - y));
        for (int i = 1; i < distanz; i++) {
            if (Schachfeld[y + dy * i][x + dx * i] != 0) return false;
        }
        return true;
    }

    private boolean checkForChecks(int x, int y, int pieceColor) {
        // Gerade Linien prüfen (Turm, Dame)
        pieceColor = pieceColor / Math.abs(pieceColor);
        int[][] rookDirections = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : rookDirections) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            while (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                int piece = Schachfeld[newY][newX];
                if (piece != 0) {
                    if (piece * pieceColor == -4 || piece * pieceColor == -5) {
                        return true; // Steht im Schach
                    }
                    break; // Figur blockiert die Sichtlinie
                }
                newX += dir[0];
                newY += dir[1];
            }
        }

        // Diagonale Linien prüfen (Läufer, Dame)
        int[][] bishopDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] dir : bishopDirections) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            while (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                int piece = Schachfeld[newY][newX];
                if (piece != 0) {
                    if (piece * pieceColor == -2 || piece * pieceColor == -5) {
                        return true; // Steht im Schach
                    }
                    break; // Figur blockiert die Sichtlinie
                }
                newX += dir[0];
                newY += dir[1];
            }
        }

        // Springer prüfen
        int[][] knightMoves = {
                {1, 2}, {2, 1}, {-1, 2}, {-2, 1},
                {1, -2}, {2, -1}, {-1, -2}, {-2, -1}
        };
        for (int[] move : knightMoves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                if (Schachfeld[newY][newX] * pieceColor == -3) {
                    return true;
                }
            }
        }

        // Bauern prüfen
        int pawnDir = (pieceColor > 0) ? -1 : 1; // Weiß schaut nach oben (-1 in Y), Schwarz nach unten (+1)
        int[] pawnCols = {-1, 1};
        for (int dc : pawnCols) {
            int newX = x + dc;
            int newY = y + pawnDir;
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                if (Schachfeld[newY][newX] * pieceColor == -1) {
                    return true;
                }
            }
        }

        // Gegnerischen König prüfen
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int newX = x + dx;
                int newY = y + dy;
                if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                    if (Schachfeld[newY][newX] * pieceColor == -6) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    Point getKingPosition(int pieceColor) {
        pieceColor = pieceColor / Math.abs(pieceColor);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Schachfeld[j][i] * pieceColor == 6) {
                    return new Point(i, j);
                }
            }
        }
        return new Point(-1, -1);
    }

    private boolean kingInCheck(int pieceColor) {
        Point KingPosition = getKingPosition(pieceColor);
        int x = KingPosition.x;
        int y = KingPosition.y;
        return checkForChecks(x, y, pieceColor);
    }

    public boolean checkCheckmate(int pieceColor) {
        if (!kingInCheck(pieceColor))
            return false;
        if (checkKingHasValidMoves(pieceColor))
            return false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //Scanne das ganze Brett für alle figuren einer farbe
                if (Schachfeld[j][i] * pieceColor > 0) {
                    //generiere PseudoMoves für die gefundene Figur
                    ArrayList<int[]> points = generatePseudoMoves( i, j);
                    for (int[] point : points) {
                        if (!(point[0] == i && point[1] == j)) {
                            int pointX = point[0];
                            int pointY = point[1];
                            if (Schachfeld[pointY][pointX] * pieceColor <= 0) {
                                if (isValidPieceMove(i, j, pointX, pointY)) {
                                    if (blocksCheck(i, j, pointX, pointY))
                                        return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public ArrayList<int[]> generatePseudoMoves(int x, int y) {
        ArrayList<int[]> returnValues = new ArrayList<>();
        int piece = Schachfeld[y][x];
        int[][] baseDirections;
        switch (piece) {
            case 1, -1:
                if (y - piece >= 0 && y - piece < 8)
                    returnValues.add(new int[]{x, y - piece});

                if (piece > 0 && y == 6 || piece < 0 && y == 1)
                    if (Schachfeld[y - piece][x] * piece == 0)
                        returnValues.add(new int[]{x, y - 2 * piece});

                if (y - piece >= 0 && y - piece < 8 && x + 1 < 8)
                    returnValues.add(new int[]{x + 1, y - piece});

                if (y - piece >= 0 && y - piece < 8 && x - 1 >= 0)
                    returnValues.add(new int[]{x - 1, y - piece});

                break;
            case 2, -2:
                baseDirections = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                for (int[] dir : baseDirections) {
                    for (int step = 1; step < 8; step++) {
                        int dx = dir[0] * step;
                        int dy = dir[1] * step;
                        if (y + dy >= 0 && y + dy < 8 && x + dx >= 0 && x + dx < 8)
                            returnValues.add(new int[]{x + dx, y + dy});
                    }
                }
                break;
            case 3, -3:
                int[][] offsets = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
                for (int[] offset : offsets) {
                    if (y + offset[0] >= 0 && y + offset[0] < 8 && x + offset[1] >= 0 && x + offset[1] < 8)
                        returnValues.add(new int[]{x + offset[1], y + offset[0]});
                }
                break;
            case 4, -4:
                baseDirections = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                for (int[] offset : baseDirections) {
                    for (int step = 1; step < 8; step++) {
                        int dy = offset[0] * step;
                        int dx = offset[1] * step;
                        if (y + dy >= 0 && y + dy < 8 && x + dx >= 0 && x + dx < 8)
                            returnValues.add(new int[]{x + dx, y + dy});
                    }
                }
                break;
            case 5, -5:
                baseDirections = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                for (int[] offset : baseDirections) {
                    for (int step = 1; step < 8; step++) {
                        int dy = offset[0] * step;
                        int dx = offset[1] * step;
                        if (y + dy >= 0 && y + dy < 8 && x + dx >= 0 && x + dx < 8)
                            returnValues.add(new int[]{x + dx, y + dy});
                    }
                }

                baseDirections = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                for (int[] dir : baseDirections) {
                    for (int step = 1; step < 8; step++) {
                        int dx = dir[0] * step;
                        int dy = dir[1] * step;
                        if (y + dy >= 0 && y + dy < 8 && x + dx >= 0 && x + dx < 8)
                            returnValues.add(new int[]{x + dx, y + dy});
                    }
                }
                break;
        }
        return returnValues;
    }

    public boolean checkKingHasValidMoves(int pieceColor) {
        pieceColor = pieceColor / Math.abs(pieceColor);
        Point KingPosition = getKingPosition(pieceColor);
        int x = KingPosition.x;
        int y = KingPosition.y;
        int impossibleTiles = 0;
        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                if (x + i >= 0 && x + i < 8 && y + j >= 0 && y + j < 8) {
                    if (i != 0 || j != 0) {
                        if (Schachfeld[y + j][x + i] * pieceColor > 0) {
                            impossibleTiles++;
                        } else {
                            int temp = Schachfeld[y + j][x + i];
                            Schachfeld[y + j][x + i] = Schachfeld[y][x];
                            Schachfeld[y][x] = 0;

                            boolean inCheck = checkForChecks(x + i, y + j, pieceColor);

                            Schachfeld[y][x] = Schachfeld[y + j][x + i];
                            Schachfeld[y + j][x + i] = temp;

                            if (inCheck)
                                impossibleTiles++;
                        }
                    }
                } else {
                    impossibleTiles++;
                }
            }
        }
        return (impossibleTiles != 8);
    }

    public boolean isWhite(int x, int y) {
        return Schachfeld[y][x] >0;
    }

    private boolean isStalemate(int pieceColor){
        if(kingInCheck(pieceColor))
            return false;
        if (checkKingHasValidMoves(pieceColor))
            return false;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(Schachfeld[j][i]*pieceColor> 0) {
                    ArrayList<int[]> possiblePoints = generatePseudoMoves( i, j);
                    for (int[] Point : possiblePoints) {
                        int pointX = Point[0];
                        int pointY = Point[1];
                        if (Schachfeld[pointY][pointX] * Schachfeld[j][i] > 0)
                            continue;
                        if (!isValidPieceMove(i, j, pointX, pointY))
                            continue;
                        if (blocksCheck(i, j, pointX, pointY))
                            return false;
                    }
                }
            }
        }
        return true;
    }


    public boolean blocksCheck(int x, int y, int toX, int toY) {
        int tempOrigin = Schachfeld[y][x];
        int tempTarget = Schachfeld[toY][toX];

        if (Math.abs(tempTarget) == 6)
            return false;

        boolean isEnPassant = (Math.abs(tempOrigin) == 1 && x != toX && tempTarget == 0);
        int epPawn = 0;

        Schachfeld[toY][toX] = tempOrigin;
        Schachfeld[y][x] = 0;

        if (isEnPassant) {
            epPawn = Schachfeld[oldToY][oldToX];
            Schachfeld[oldToY][oldToX] = 0;
        }

        boolean inCheck = kingInCheck(tempOrigin);

        Schachfeld[y][x] = tempOrigin;
        Schachfeld[toY][toX] = tempTarget;

        if (isEnPassant) {
            Schachfeld[oldToY][oldToX] = epPawn;
        }
        return !inCheck;
    }

    public void choosePiece(int Piece) {
        Schachfeld[oldToY][oldToX] = Piece;
        chooseNewPiece = false;
        whiteMoves = !whiteMoves;
    }

    private boolean validRochade(int x, int y, int toX, int toY) {
        if(toX==2)toX = 0;
        if(toX==6)toX = 7;
        if (Schachfeld[toY][toX] * Schachfeld[y][x] <= 0) return false;
        if (UsedTiles[toY][toX] == 1 || UsedTiles[y][x] == 1) return false;
        if (Math.abs(Schachfeld[y][x]) == 6 && Math.abs(Schachfeld[toY][toX]) == 4) {
            int dx = Integer.compare(toX, x);
            int distanz = Math.max(Math.abs(toX - x), Math.abs(toY - y));

            if (checkForChecks(x, y, Schachfeld[y][x])) return false;

            for (int i = 1; i < distanz; i++) {
                if (Schachfeld[y][x + dx * i] != 0) return false;
                if (i <= 2 && checkForChecks(x + dx * i, y, Schachfeld[y][x])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
