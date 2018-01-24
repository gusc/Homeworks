/**
 * MD2
 *
 * @author Gusts Kaksis, gk17025
 */
import java.util.*;

public class Domino12
{

    /**
     * This class holds an abstraction of a single domino tile
     */
    private static class Tile
    {
        private int left;
        private int right;

        public Tile(int left, int right)
        {
            this.left = left;
            this.right = right;
        }

        public boolean equals(Tile o)
        {
            return (this.left == o.left && this.right == o.right);
        }

        public String toString()
        {
            return this.left + "-" + this.right;
        }
    }

    /**
     * This is a base class that holds a tile set (and can print it to string)
     */
    private static class TileSet
    {
        protected ArrayList<Tile> tileSet = new ArrayList<>();

        public TileSet()
        {

        }

        public void Add(Tile t)
        {
            this.tileSet.add(t);
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            for (Tile t : this.tileSet)
            {
                sb.append(" ");
                sb.append(t);
            }
            return sb.toString();
        }

    }

    /**
     * This is a class containing all the tiles in the game and is able to deal them away to players
     */
    private static class AllTileSet extends TileSet
    {
        public AllTileSet()
        {
            for (int i = 0; i <= 12; i ++)
            {
                for (int j = i; j <= 12; j ++)
                {
                    Add(new Tile(i, j));
                }
            }
        }

        public TileSet[] dealTo(int numPlayers)
        {
            int numTiles = this.tileSet.size();
            int perPlayer = Math.floorDiv(numTiles, numPlayers);

            // Allocate each player's set
            TileSet[] playerTiles = new TileSet[numPlayers];

            for (int i = 0; i < numPlayers; i ++)
            {
                playerTiles[i] = new TileSet();
                for (int j = 0; j < perPlayer; j ++)
                {
                    // Calculate a random index
                    int x = (int)(this.tileSet.size() * Math.random());
                    // Remove the tile from the set
                    Tile t = this.tileSet.remove(x);
                    // Add the tile to player's set
                    playerTiles[i].Add(t);
                }
            }

            return playerTiles;
        }
    }

    /**
     * Create a new tile set, deal it to players and print the results to screen
     * @param numPlayers
     */
    private static void deal(int numPlayers)
    {
        // Create a tile set
        AllTileSet set = new AllTileSet();

        // Deal it to number of players
        TileSet[] players = set.dealTo(numPlayers);

        // Print the results
        for (int i = 0; i < players.length; i ++)
        {
            System.out.format("%d.speletajam ir:%s\n", (i + 1), players[i]);
        }
    }

    public static void main(String[] args)
    {
        int N = 0;

        // Better safe than sorry :)
        try
        {
            N = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e)
        {
            // Input is not a number
            return;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            // Not enough arguments
            return;
        }

        // Deal the tiles
        deal(N);
    }
}
