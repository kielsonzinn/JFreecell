package com.sri.jfreecell;

import com.sri.jfreecell.event.GameEvents;
import com.sri.jfreecell.event.GameListenerImpl;
import com.sri.jfreecell.event.MenuActionListener;
import com.sri.jfreecell.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

import static com.sri.jfreecell.event.MenuActionListener.MenuAction.*;
import static com.sri.jfreecell.util.FileUtil.*;
import static java.awt.event.ActionEvent.ALT_MASK;
import static java.awt.event.ActionEvent.CTRL_MASK;
import static java.awt.event.KeyEvent.*;
import static javax.swing.JOptionPane.*;



/**
 * Main class for FreeCell. Free Cell solitaire program. Main program / JFrame.
 * Adds a few components and the main graphics area, UICardPanel, that handles
 * the mouse and painting.
 *
 * @author Sateesh Gampala
 * @version 5.0.1
 */
public class UIFreeCell extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final ClassLoader CLSLDR = UIFreeCell.class.getClassLoader();
    private static final ImageIcon icon = new ImageIcon(CLSLDR.getResource("cardimages/icon.png"));
    JLabel label1 = new JLabel("                                  Pilha de descanso                                                                             Pilha de descarte",  JLabel.LEFT);
    private static final Color BACKGROUND_COLOR = new Color(0, 110, 135);


    public GameModel model;

    public static final String version = "5.2.10";

    private JLabel cardCount;

    private static final int PORT = 6789;
    private static ServerSocket socket;

    public static void main(String[] args) {
        checkIfRunning();
        checkLoadWindowsStyle();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UIFreeCell();
            }
        });
    }

    private static void checkLoadWindowsStyle() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public UIFreeCell() {
        checkAndLoadGame();
        UICardPanel boardDisplay = new UICardPanel(model);
        model.addGameListener(new GameListenerImpl(this));

        cardCount = new JLabel("52 ", SwingConstants.RIGHT);
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(new JLabel("Cards Left:"));
        controlPanel.add(cardCount);

        label1.setPreferredSize(new Dimension(50, 15));
        label1.setVerticalAlignment(JLabel.TOP);
        label1.setBackground(BACKGROUND_COLOR);
        label1.setOpaque(true);
        label1.setForeground(Color.white);
        label1.setFont(new Font("Serif", Font.PLAIN, 14));


        JPanel content = new JPanel();

        content.setLayout(new BorderLayout());
        label1.setVisible(true);
        content.add(label1, BorderLayout.NORTH);


        content.add(controlPanel, BorderLayout.SOUTH);

        content.add(boardDisplay, BorderLayout.CENTER);



        setContentPane(content);
        setJMenuBar(createMenu());
        setTitle("FreeCell #" + model.gameNo);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit(model.getState().equals(GameEvents.COMPLETE));
                e.getWindow().dispose();
            }
        });
        setIconImage(icon.getImage());
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
        // setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        this.model.notifyChanges();

    }

    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        menu.setMnemonic(VK_G);
        menuBar.add(menu);

        createMenuItem(menu, NEW, VK_N, VK_F2, 0);
        createMenuItem(menu, SELECT, VK_S, VK_F3, 0);
        createMenuItem(menu, RESTART, VK_R, VK_R, CTRL_MASK);
        menu.addSeparator();
        createMenuItem(menu, UNDO, VK_U, VK_Z, CTRL_MASK);
        createMenuItem(menu, HINT, VK_H, VK_H, 0);
        menu.addSeparator();
        createMenuItem(menu, STATISTICS, VK_T, VK_F4, 0).setEnabled(false);
        createMenuItem(menu, OPTIONS, VK_O, VK_F5, 0).setEnabled(false);
        menu.addSeparator();
        createMenuItem(menu, EXIT, VK_X, VK_X, ALT_MASK);

        menu = new JMenu("Help");
        menu.setMnemonic(VK_H);
        menuBar.add(menu);

        createMenuItem(menu, HELP, VK_J, VK_F1, 0);
        createMenuItem(menu, ABOUT, VK_A, 0, 0);
        menuBar.add(menu);

        return menuBar;
    }

    private JMenuItem createMenuItem(JMenu parent, String menuName, int mnemonic, int keyCode, int modifiers) {
        JMenuItem menuItem = new JMenuItem(menuName, mnemonic);
        if (keyCode > 0)
            menuItem.setAccelerator(KeyStroke.getKeyStroke(keyCode, modifiers));
        menuItem.addActionListener(new MenuActionListener(this));
        parent.add(menuItem);
        return menuItem;
    }

    public void updateCardCount(int count) {
        cardCount.setText(count + " ");
    }

    public void loadRandGame() {
        model.loadRandGame();
        setTitle("FreeCell #" + model.gameNo);
    }

    /**
     * Shows window to select game.
     */
    public void selectGame() {
        int gameNo = 1;
        JLabel bLabel = new JLabel("Select a game number from 1 to 32000");
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(bLabel);
        String userInput = showInputDialog(this, panel, "Game Number", PLAIN_MESSAGE);
        if (userInput == null)
            return;
        try {
            gameNo = Integer.parseInt(userInput);
            if (gameNo < 1 || gameNo > 32000)
                throw new Exception();
        } catch (Exception e) {
            showMessageDialog(this, "Invalid game number! Try again.");
            return;
        }
        setTitle("FreeCell #" + gameNo);
        model.loadGame(gameNo);
    }

    /**
     * Create and show About window.
     */
    public void showAbout() {
        icon.setImage(ImageUtil.getScaledImage(icon.getImage(), 40, 40));
        JLabel aLabel = new JLabel("<html>FreeCell<br> v" + version + "</html>", icon, JLabel.LEFT);
        JLabel bLabel = new JLabel("<html>\u00a9 2016-17 Sateesh Chandra G<br>All rights reserved.</html>");
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(aLabel);
        panel.add(bLabel);
        showMessageDialog(this, panel, "About FreeCell", PLAIN_MESSAGE);
    }

    public void showHelp() {



        JLabel bLabel = new JLabel("<html>Características<br>"
                + "Baralhos: 1;<br>"
                + "Dificuldade: Fácil;<br>"
                + "Tempo: Médio;<br>"
                + "Tipo: Habilidade;<br>"
                + "Objetivo<br>"
                + "O objetivo é mover todas as cartas para as fundações em ordem crescente no naipe usando o menor número possível de movimentos.<br>"
                + "<br>"
                + "O jogo<br>"
                + "Fundações<br>"
                + "Existem 4 fundações (canto superior direito);<br>"
                + "As fundações aceitam as cartas em ordem crescente e com o mesmo naipe.<br>"
                + "Células<br>"
                + "São 4 células ao todo (canto superior esquerdo);<br>"
                + "As células vazias são utilizadas como espaços temporários para fazer movimentos de pilhas e para jogadas estratégicas.<br>"
                + "Pilhas<br>"
                + "O jogo tem 8 fundações (parte inferior);<br>"
                + "As cartas nas pilhas devem ser organizadas em sequência decrescente e com cores alternadas;<br>"
                + "Pode-se mover um conjunto de cartas desde que elas estejam em sequéncia e existam células vazias e/ou espaços vazios para realizar o movimento;<br>"
                + "Pilhas vazias podem ser ocupadas por qualquer carta ou conjunto de cartas em ordem decrescente e com cores alternadas.</html>");
        JPanel panel = new JPanel(new GridLayout(1,0));
        panel.add(bLabel);

        showMessageDialog(this, panel, "Help", PLAIN_MESSAGE);
    }

    /**
     * Checks if any other instance is already running.
     */
    private static void checkIfRunning() {
        try {
            socket = new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
        } catch (BindException e) {
            System.err.println("Found another JFreeCell instance is running.");
            showMessageDialog(null, "Looks like another instance of Freecell is already running.", "Alert", ERROR_MESSAGE);
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Unexpected error.");
            e.printStackTrace();
            System.exit(2);
        }
    }

    private void checkAndLoadGame() {
        GameModel model = (GameModel) getObjectfromFile(STATE_FILE);
        if (model != null) {
            this.model = model;
            deleteFile(STATE_FILE);
        } else {
            this.model = new GameModel();
        }
    }

    public void exit(boolean isGameComplete) {
        if(!isGameComplete) {
            saveObjecttoFile(model, STATE_FILE);
        }
        System.exit(0);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
