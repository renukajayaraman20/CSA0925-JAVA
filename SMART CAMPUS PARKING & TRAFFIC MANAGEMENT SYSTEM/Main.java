package com.campuspulse;

import com.campuspulse.db.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/*
 * ============================================================
 * CAMPUSPULSE
 * Saveetha University Smart Parking Intelligence
 *
 * SINGLE FILE:
 * Main.java
 *
 * Contains:
 * Main
 * CampusPulseFrame
 * CampusMap
 * CameraCard
 * CameraView
 * ZoneBarChart
 * RevenuePanel
 * ============================================================
 */

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            new CampusPulseFrame().setVisible(true);
        });
    }
}


/* ============================================================
   MAIN APPLICATION FRAME
   ============================================================ */

class CampusPulseFrame extends JFrame {

    /* ---------- COLORS ---------- */

    static final Color NAV = new Color(18, 27, 49);
    static final Color NAV2 = new Color(31, 46, 78);

    static final Color BG = new Color(245, 248, 252);
    static final Color CARD = Color.WHITE;

    static final Color TEXT = new Color(27, 38, 60);
    static final Color MUTED = new Color(103, 116, 139);

    static final Color BLUE = new Color(58, 104, 235);
    static final Color CYAN = new Color(31, 181, 190);
    static final Color GREEN = new Color(43, 178, 113);
    static final Color ORANGE = new Color(242, 157, 55);
    static final Color RED = new Color(224, 76, 92);
    static final Color PURPLE = new Color(126, 91, 210);

    static final Color LINE = new Color(226, 231, 240);

    /* ---------- LAYOUT ---------- */

    final CardLayout pages = new CardLayout();
    final JPanel pageHost = new JPanel(pages);

    final JLabel pageTitle = new JLabel();
    final JLabel clock = new JLabel();
    final JLabel dbStatus = new JLabel();

    /* ---------- DATA ---------- */

    final List<Zone> zones = new ArrayList<>();
    final Map<Integer, Slot> slots = new LinkedHashMap<>();
    final List<Vehicle> vehicles = new ArrayList<>();
    final List<Session> sessions = new ArrayList<>();
    final List<Reservation> reservations = new ArrayList<>();
    final List<Violation> violations = new ArrayList<>();

    int selectedZone = 0;

    /* ---------- CONSTRUCTOR ---------- */

    CampusPulseFrame() {

        setTitle(
                "CampusPulse • Saveetha University Smart Parking Intelligence"
        );

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1440, 900);
        setMinimumSize(new Dimension(1180, 760));

        setLocationRelativeTo(null);

        seedDemoData();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildWorkspace(), BorderLayout.CENTER);

        setContentPane(root);

        showPage(
                "Command Center",
                buildDashboard()
        );

        new javax.swing.Timer(
                1000,
                e -> clock.setText(
                        LocalTime.now().format(
                                DateTimeFormatter.ofPattern("HH:mm:ss")
                        )
                )
        ).start();
    }


    /* ========================================================
       DEMO DATA
       ======================================================== */

    void seedDemoData() {

        String[] names = {
                "Engineering",
                "AHS",
                "Medical",
                "Rectangular",
                "SCAD",
                "SSPE"
        };

        int[] caps = {
                100,
                70,
                110,
                75,
                55,
                55
        };

        int[] occ = {
                68,
                34,
                91,
                62,
                28,
                21
        };

        for (int i = 0; i < names.length; i++) {

            zones.add(
                    new Zone(
                            i,
                            names[i],
                            caps[i],
                            occ[i]
                    )
            );
        }

        int id = 1;

        for (Zone z : zones) {

            for (int n = 1; n <= z.capacity; n++) {

                boolean occupied = n <= z.occupied;

                String category;

                if (n % 25 == 0) {
                    category = "ACCESSIBLE";
                } else if (n % 20 == 0) {
                    category = "EV";
                } else if (n % 33 == 0) {
                    category = "SERVICE";
                } else if (n % 50 == 0) {
                    category = "EMERGENCY";
                } else {
                    category = "GENERAL";
                }

                slots.put(
                        id,
                        new Slot(
                                id,
                                z.id,
                                String.format(
                                        "%s-%03d",
                                        z.code(),
                                        n
                                ),
                                occupied,
                                category
                        )
                );

                id++;
            }
        }

        /* Demo vehicles */

        vehicles.add(
                new Vehicle(
                        1,
                        "TN01AB1234",
                        "CAR",
                        "STUDENT",
                        "Engineering",
                        false
                )
        );

        vehicles.add(
                new Vehicle(
                        2,
                        "TN02CD5678",
                        "EV",
                        "FACULTY",
                        "Medical",
                        true
                )
        );

        vehicles.add(
                new Vehicle(
                        3,
                        "TN03EF9012",
                        "BIKE",
                        "STUDENT",
                        "AHS",
                        false
                )
        );

        /* Demo sessions */

        sessions.add(
                new Session(
                        1001,
                        1,
                        "ENG-069",
                        "Engineering",
                        LocalDateTime.now().minusMinutes(42),
                        true,
                        0
                )
        );

        sessions.add(
                new Session(
                        1002,
                        2,
                        "MED-090",
                        "Medical",
                        LocalDateTime.now().minusMinutes(25),
                        true,
                        0
                )
        );

        /* Demo reservations */

        reservations.add(
                new Reservation(
                        1,
                        "TN01AB1234",
                        "ENG-075",
                        "Engineering",
                        LocalDateTime.now().plusHours(2),
                        "CONFIRMED"
                )
        );

        reservations.add(
                new Reservation(
                        2,
                        "TN02CD5678",
                        "MED-105",
                        "Medical",
                        LocalDateTime.now().plusHours(3),
                        "CONFIRMED"
                )
        );

        /* Demo violations */

        violations.add(
                new Violation(
                        1,
                        "TN01AB1234",
                        "No Parking",
                        500,
                        "PENDING"
                )
        );

        violations.add(
                new Violation(
                        2,
                        "TN03EF9012",
                        "Wrong Zone",
                        300,
                        "RESOLVED"
                )
        );
    }


    /* ========================================================
       SIDEBAR
       ======================================================== */

    JPanel buildSidebar() {

        JPanel side = new JPanel(new BorderLayout());

        side.setPreferredSize(
                new Dimension(255, 900)
        );

        side.setBackground(NAV);


        /* ---------- BRAND ---------- */

        JPanel brand = new JPanel();

        brand.setOpaque(false);

        brand.setLayout(
                new BoxLayout(
                        brand,
                        BoxLayout.Y_AXIS
                )
        );

        brand.setBorder(
                new EmptyBorder(
                        25,
                        22,
                        18,
                        18
                )
        );


        JLabel logo = new JLabel("CP");

        logo.setOpaque(true);
        logo.setBackground(BLUE);
        logo.setForeground(Color.WHITE);

        logo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        logo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        logo.setPreferredSize(
                new Dimension(52, 52)
        );

        logo.setMaximumSize(
                new Dimension(52, 52)
        );

        brand.add(logo);

        brand.add(
                Box.createVerticalStrut(12)
        );


        JLabel title = new JLabel("CampusPulse");

        title.setForeground(Color.WHITE);

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        brand.add(title);


        JLabel sub = new JLabel(
                "SAVEETHA UNIVERSITY"
        );

        sub.setForeground(
                new Color(220, 226, 240)
        );

        sub.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        10
                )
        );

        brand.add(sub);

        side.add(
                brand,
                BorderLayout.NORTH
        );


        /* ---------- NAVIGATION ---------- */

        JPanel nav = new JPanel();

        nav.setOpaque(false);

        nav.setBorder(
                new EmptyBorder(
                        8,
                        12,
                        8,
                        12
                )
        );

        nav.setLayout(
                new BoxLayout(
                        nav,
                        BoxLayout.Y_AXIS
                )
        );


        String[][] menu = {

                {"⌂", "Command Center", "Dashboard"},
                {"◈", "Campus Intelligence", "Map"},
                {"◉", "CCTV Network", "CCTV"},
                {"▦", "Live Slot Grid", "Slots"},
                {"✦", "Smart Allocation", "Allocation"},
                {"▣", "Reservations", "Reservations"},
                {"⇄", "Entry / Exit", "Sessions"},
                {"₹", "Payments & Passes", "Payments"},
                {"!", "Violations", "Violations"},
                {"▥", "Analytics & Reports", "Reports"},
                {"⚙", "System & Database", "Settings"}
        };


        for (String[] m : menu) {

            JButton b = navButton(
                    m[0],
                    m[1]
            );

            b.addActionListener(e -> {

                switch (m[2]) {

                    case "Dashboard" ->
                            showPage(
                                    "Command Center",
                                    buildDashboard()
                            );

                    case "Map" ->
                            showPage(
                                    "Campus Intelligence",
                                    buildMap()
                            );

                    case "CCTV" ->
                            showPage(
                                    "CCTV Network",
                                    buildCCTV()
                            );

                    case "Slots" ->
                            showPage(
                                    "Live Slot Grid",
                                    buildSlots()
                            );

                    case "Allocation" ->
                            showPage(
                                    "Smart Allocation",
                                    buildAllocation()
                            );

                    case "Reservations" ->
                            showPage(
                                    "Reservations",
                                    buildReservations()
                            );

                    case "Sessions" ->
                            showPage(
                                    "Entry / Exit",
                                    buildSessions()
                            );

                    case "Payments" ->
                            showPage(
                                    "Payments & Passes",
                                    buildPayments()
                            );

                    case "Violations" ->
                            showPage(
                                    "Violations",
                                    buildViolations()
                            );

                    case "Reports" ->
                            showPage(
                                    "Analytics & Reports",
                                    buildReports()
                            );

                    case "Settings" ->
                            showPage(
                                    "System & Database",
                                    buildSettings()
                            );
                }
            });

            nav.add(b);

            nav.add(
                    Box.createVerticalStrut(5)
            );
        }

        side.add(
                nav,
                BorderLayout.CENTER
        );


        /* ---------- FOOTER ---------- */

        JPanel foot = new JPanel(
                new BorderLayout()
        );

        foot.setOpaque(false);

        foot.setBorder(
                new EmptyBorder(
                        12,
                        18,
                        18,
                        18
                )
        );

        dbStatus.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        updateDbStatus();

        foot.add(
                dbStatus,
                BorderLayout.WEST
        );

        side.add(
                foot,
                BorderLayout.SOUTH
        );

        return side;
    }


    /* ========================================================
       FIXED NAVIGATION BUTTON
       ======================================================== */

    JButton navButton(
            String icon,
            String text
    ) {

        JButton b = new JButton();

        /*
         * IMPORTANT:
         * BasicButtonUI prevents Windows/System Look & Feel
         * from changing the button into a white button.
         */

        b.setUI(new BasicButtonUI());

        b.setText(
                "<html>" +
                        "<span style='font-size:16px'>" +
                        icon +
                        "</span>" +
                        "&nbsp;&nbsp;" +
                        text +
                        "</html>"
        );

        b.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        b.setMaximumSize(
                new Dimension(
                        225,
                        42
                )
        );

        b.setPreferredSize(
                new Dimension(
                        225,
                        42
                )
        );

        b.setMinimumSize(
                new Dimension(
                        225,
                        42
                )
        );

        b.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        /* DARK BACKGROUND */

        b.setBackground(NAV);

        /* LIGHT TEXT */

        b.setForeground(
                new Color(
                        220,
                        226,
                        240
                )
        );

        /*
         * These four lines are the main fix
         * for your white navigation tabs.
         */

        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);

        b.setBorder(
                new EmptyBorder(
                        9,
                        14,
                        9,
                        8
                )
        );


        /* HOVER */

        b.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {

                        b.setBackground(NAV2);

                        b.setForeground(
                                Color.WHITE
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {

                        b.setBackground(NAV);

                        b.setForeground(
                                new Color(
                                        220,
                                        226,
                                        240
                                )
                        );
                    }
                }
        );

        return b;
    }


    /* ========================================================
       WORKSPACE
       ======================================================== */

    JPanel buildWorkspace() {

        JPanel p = new JPanel(
                new BorderLayout()
        );

        p.setBackground(BG);


        JPanel top = new JPanel(
                new BorderLayout()
        );

        top.setBackground(Color.WHITE);

        top.setBorder(
                new CompoundBorder(
                        new MatteBorder(
                                0,
                                0,
                                1,
                                0,
                                LINE
                        ),
                        new EmptyBorder(
                                14,
                                25,
                                14,
                                25
                        )
                )
        );


        pageTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        pageTitle.setForeground(TEXT);

        top.add(
                pageTitle,
                BorderLayout.WEST
        );


        JPanel right = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        16,
                        0
                )
        );

        right.setOpaque(false);


        JLabel liveLabel = new JLabel(
                "● SYSTEM LIVE"
        );

        liveLabel.setForeground(GREEN);

        liveLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        right.add(liveLabel);


        clock.setFont(
                new Font(
                        "Consolas",
                        Font.BOLD,
                        14
                )
        );

        clock.setForeground(TEXT);

        right.add(clock);

        top.add(
                right,
                BorderLayout.EAST
        );


        p.add(
                top,
                BorderLayout.NORTH
        );


        pageHost.setBackground(BG);

        p.add(
                pageHost,
                BorderLayout.CENTER
        );

        return p;
    }


    /* ========================================================
       PAGE HANDLING
       ======================================================== */

    void showPage(
            String title,
            JPanel panel
    ) {

        pageTitle.setText(title);

        pageHost.removeAll();

        pageHost.add(
                panel,
                "page"
        );

        pageHost.revalidate();
        pageHost.repaint();
    }


    JPanel page() {

        JPanel p = new JPanel(
                new BorderLayout(
                        18,
                        18
                )
        );

        p.setBackground(BG);

        p.setBorder(
                new EmptyBorder(
                        22,
                        25,
                        25,
                        25
                )
        );

        return p;
    }


    JPanel card() {

        JPanel p = new JPanel();

        p.setBackground(CARD);

        p.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                LINE,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                18,
                                18,
                                18,
                                18
                        )
                )
        );

        return p;
    }


    JLabel h(
            String s,
            int size
    ) {

        JLabel l = new JLabel(s);

        l.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        size
                )
        );

        l.setForeground(TEXT);

        return l;
    }


    JLabel muted(String s) {

        JLabel l = new JLabel(s);

        l.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        l.setForeground(MUTED);

        return l;
    }


    JButton primary(String s) {

        JButton b = new JButton(s);

        b.setUI(new BasicButtonUI());

        b.setFocusPainted(false);

        b.setOpaque(true);

        b.setContentAreaFilled(true);

        b.setBorderPainted(false);

        b.setForeground(Color.WHITE);

        b.setBackground(BLUE);

        b.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        b.setBorder(
                new EmptyBorder(
                        10,
                        16,
                        10,
                        16
                )
        );

        return b;
    }


    JButton secondary(String s) {

        JButton b = new JButton(s);

        b.setUI(new BasicButtonUI());

        b.setOpaque(true);

        b.setContentAreaFilled(true);

        b.setBorderPainted(false);

        b.setFocusPainted(false);

        b.setBackground(
                new Color(
                        239,
                        243,
                        251
                )
        );

        b.setForeground(TEXT);

        b.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        b.setBorder(
                new EmptyBorder(
                        10,
                        16,
                        10,
                        16
                )
        );

        return b;
    }


    /* ========================================================
       DASHBOARD
       ======================================================== */

    JPanel buildDashboard() {

        JPanel p = page();


        JPanel welcome = card();

        welcome.setLayout(
                new BorderLayout()
        );


        JPanel wt = new JPanel();

        wt.setOpaque(false);

        wt.setLayout(
                new BoxLayout(
                        wt,
                        BoxLayout.Y_AXIS
                )
        );


        wt.add(
                h(
                        "Smart Parking Command Center",
                        28
                )
        );

        wt.add(
                Box.createVerticalStrut(6)
        );

        wt.add(
                muted(
                        "Destination-aware parking • real-time occupancy • intelligent allocation"
                )
        );


        welcome.add(
                wt,
                BorderLayout.WEST
        );


        JLabel badge = new JLabel(
                "  6 ZONES  •  465 SLOTS  •  MIXED-USE  "
        );

        badge.setOpaque(true);

        badge.setBackground(
                new Color(
                        232,
                        242,
                        255
                )
        );

        badge.setForeground(BLUE);

        badge.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        badge.setBorder(
                new EmptyBorder(
                        9,
                        12,
                        9,
                        12
                )
        );

        welcome.add(
                badge,
                BorderLayout.EAST
        );


        p.add(
                welcome,
                BorderLayout.NORTH
        );


        JPanel stats = new JPanel(
                new GridLayout(
                        1,
                        4,
                        14,
                        0
                )
        );

        stats.setOpaque(false);


        int total = slots.size();

        int occ =
                (int) slots.values()
                        .stream()
                        .filter(s -> s.occupied)
                        .count();

        int av = total - occ;


        stats.add(
                stat(
                        "TOTAL CAPACITY",
                        String.valueOf(total),
                        "6 campus zones",
                        BLUE,
                        "▦"
                )
        );

        stats.add(
                stat(
                        "AVAILABLE NOW",
                        String.valueOf(av),
                        String.format(
                                "%.0f%% free",
                                (av * 100.0) / total
                        ),
                        GREEN,
                        "✓"
                )
        );

        stats.add(
                stat(
                        "OCCUPIED",
                        String.valueOf(occ),
                        String.format(
                                "%.0f%% utilized",
                                (occ * 100.0) / total
                        ),
                        RED,
                        "●"
                )
        );

        stats.add(
                stat(
                        "ACTIVE SESSIONS",
                        String.valueOf(
                                sessions.stream()
                                        .filter(s -> s.active)
                                        .count()
                        ),
                        "vehicles inside campus",
                        ORANGE,
                        "↔"
                )
        );


        p.add(
                stats,
                BorderLayout.CENTER
        );


        JPanel bottom = new JPanel(
                new GridLayout(
                        1,
                        2,
                        14,
                        0
                )
        );

        bottom.setOpaque(false);

        bottom.add(zoneOverview());

        bottom.add(quickActions());


        p.add(
                bottom,
                BorderLayout.SOUTH
        );

        return p;
    }


    JPanel stat(
            String name,
            String value,
            String note,
            Color c,
            String icon
    ) {

        JPanel p = card();

        p.setLayout(
                new BorderLayout()
        );


        JLabel i = new JLabel(icon);

        i.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        25
                )
        );

        i.setForeground(c);

        p.add(
                i,
                BorderLayout.WEST
        );


        JPanel x = new JPanel();

        x.setOpaque(false);

        x.setLayout(
                new BoxLayout(
                        x,
                        BoxLayout.Y_AXIS
                )
        );


        JLabel nameLabel = new JLabel(name);

        nameLabel.setForeground(TEXT);

        nameLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        x.add(nameLabel);

        x.add(
                Box.createVerticalStrut(4)
        );


        JLabel v = h(
                value,
                28
        );

        x.add(v);

        x.add(
                Box.createVerticalStrut(2)
        );

        x.add(
                muted(note)
        );


        p.add(
                x,
                BorderLayout.CENTER
        );

        return p;
    }


    JPanel zoneOverview() {

        JPanel p = card();

        p.setLayout(
                new BorderLayout(
                        10,
                        12
                )
        );

        p.add(
                h(
                        "Zone Occupancy",
                        18
                ),
                BorderLayout.NORTH
        );


        JPanel g = new JPanel();

        g.setOpaque(false);

        g.setLayout(
                new BoxLayout(
                        g,
                        BoxLayout.Y_AXIS
                )
        );


        for (Zone z : zones) {

            JPanel row = new JPanel(
                    new BorderLayout(
                            10,
                            0
                    )
            );

            row.setOpaque(false);

            row.setMaximumSize(
                    new Dimension(
                            600,
                            38
                    )
            );


            JLabel n = new JLabel(
                    z.name() + " Zone"
            );

            n.setForeground(TEXT);

            n.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            12
                    )
            );

            n.setPreferredSize(
                    new Dimension(
                            150,
                            30
                    )
            );


            JProgressBar bar =
                    new JProgressBar(
                            0,
                            z.capacity
                    );

            bar.setValue(z.occupied);

            bar.setStringPainted(true);

            bar.setString(
                    z.occupied +
                            " / " +
                            z.capacity
            );

            bar.setForeground(
                    z.rate() > 80
                            ? RED
                            : z.rate() > 65
                              ? ORANGE
                              : GREEN
            );

            bar.setBackground(
                    new Color(
                            235,
                            239,
                            245
                    )
            );


            row.add(
                    n,
                    BorderLayout.WEST
            );

            row.add(
                    bar,
                    BorderLayout.CENTER
            );


            g.add(row);

            g.add(
                    Box.createVerticalStrut(7)
            );
        }


        p.add(
                g,
                BorderLayout.CENTER
        );

        return p;
    }


    JPanel quickActions() {

        JPanel p = card();

        p.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );


        p.add(
                h(
                        "Operational Shortcuts",
                        18
                ),
                BorderLayout.NORTH
        );


        JPanel g = new JPanel(
                new GridLayout(
                        3,
                        2,
                        10,
                        10
                )
        );

        g.setOpaque(false);


        String[] a = {
                "🧠 Find Best Slot",
                "🚗 Register Vehicle",
                "🎫 New Reservation",
                "⇄ Record Entry / Exit",
                "📹 Open CCTV",
                "📊 Generate Report"
        };


        for (String s : a) {

            JButton b = secondary(s);

            b.addActionListener(e -> {

                if (s.contains("Slot")) {

                    showPage(
                            "Smart Allocation",
                            buildAllocation()
                    );

                } else if (s.contains("Vehicle")) {

                    registerVehicleDialog();

                } else if (s.contains("Reservation")) {

                    showPage(
                            "Reservations",
                            buildReservations()
                    );

                } else if (s.contains("Entry")) {

                    showPage(
                            "Entry / Exit",
                            buildSessions()
                    );

                } else if (s.contains("CCTV")) {

                    showPage(
                            "CCTV Network",
                            buildCCTV()
                    );

                } else {

                    showPage(
                            "Analytics & Reports",
                            buildReports()
                    );
                }
            });

            g.add(b);
        }


        p.add(
                g,
                BorderLayout.CENTER
        );

        return p;
    }


    /* ========================================================
       CAMPUS MAP
       ======================================================== */

    JPanel buildMap() {

        JPanel p = page();


        JPanel head = card();

        head.setLayout(
                new BorderLayout()
        );


        head.add(
                h(
                        "Campus Digital Twin",
                        22
                ),
                BorderLayout.WEST
        );


        head.add(
                muted(
                        "Destination buildings are connected to their preferred mixed-use parking zones."
                ),
                BorderLayout.SOUTH
        );


        p.add(
                head,
                BorderLayout.NORTH
        );


        p.add(
                new CampusMap(
                        zones,
                        this
                ),
                BorderLayout.CENTER
        );


        return p;
    }


    /* ========================================================
       CCTV
       ======================================================== */

    JPanel buildCCTV() {

        JPanel p = page();


        JPanel top = card();

        top.setLayout(
                new BorderLayout(
                        12,
                        0
                )
        );


        top.add(
                h(
                        "CCTV Network • Full Campus Monitoring Wall",
                        21
                ),
                BorderLayout.WEST
        );


        JLabel live = new JLabel(
                "● LIVE • " +
                        slots.size() +
                        " SLOT CAMERAS • " +
                        vehicles.size() +
                        " REGISTERED VEHICLES"
        );

        live.setForeground(Color.BLACK);

        live.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );


        top.add(
                live,
                BorderLayout.EAST
        );


        p.add(
                top,
                BorderLayout.NORTH
        );


        JPanel cams = new JPanel(
                new GridLayout(
                        0,
                        4,
                        12,
                        12
                )
        );

        cams.setOpaque(false);


        for (Slot s : slots.values()) {

            cams.add(
                    new CameraCard(
                            s,
                            this
                    )
            );
        }


        JScrollPane sp =
                new JScrollPane(cams);

        sp.setBorder(null);

        sp.getViewport()
                .setBackground(BG);


        p.add(
                sp,
                BorderLayout.CENTER
        );


        return p;
    }


    /* ========================================================
       SLOT GRID
       ======================================================== */

    JPanel buildSlots() {

        JPanel p = page();


        JPanel toolbar = card();

        toolbar.setLayout(
                new FlowLayout(
                        FlowLayout.LEFT,
                        10,
                        0
                )
        );


        JComboBox<String> cb =
                new JComboBox<>(
                        zones.stream()
                                .map(z ->
                                        z.name() +
                                                " Zone"
                                )
                                .toArray(
                                        String[]::new
                                )
                );


        toolbar.add(
                h(
                        "Live Slot Grid",
                        20
                )
        );

        toolbar.add(
                Box.createHorizontalStrut(18)
        );

        toolbar.add(
                new JLabel("Zone")
        );

        toolbar.add(cb);


        JButton refresh =
                secondary(
                        "↻ Refresh"
                );

        toolbar.add(refresh);


        p.add(
                toolbar,
                BorderLayout.NORTH
        );


        JPanel grid = new JPanel(
                new GridLayout(
                        0,
                        8,
                        8,
                        8
                )
        );

        grid.setOpaque(false);


        Runnable draw = () -> {

            grid.removeAll();

            int zi =
                    cb.getSelectedIndex();

            for (Slot s : slots.values()) {

                if (s.zoneId == zi) {

                    grid.add(
                            new SlotTile(s)
                    );
                }
            }

            grid.revalidate();
            grid.repaint();
        };


        cb.addActionListener(
                e -> draw.run()
        );

        refresh.addActionListener(
                e -> draw.run()
        );

        draw.run();


        JScrollPane sp =
                new JScrollPane(grid);

        sp.setBorder(null);

        sp.getViewport()
                .setBackground(BG);


        p.add(
                sp,
                BorderLayout.CENTER
        );


        return p;
    }


    /* ========================================================
       SMART ALLOCATION
       ======================================================== */

    JPanel buildAllocation() {

        JPanel p = page();


        JPanel left = card();

        left.setLayout(
                new GridBagLayout()
        );


        GridBagConstraints g =
                new GridBagConstraints();

        g.insets =
                new Insets(
                        7,
                        5,
                        7,
                        5
                );

        g.fill =
                GridBagConstraints.HORIZONTAL;

        g.weightx = 1;


        JLabel info =
                h(
                        "CAPA — Context-Aware Parking Allocation",
                        20
                );


        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;

        left.add(
                info,
                g
        );


        g.gridy++;


        left.add(
                muted(
                        "The system prioritizes destination proximity, availability and special compatibility. No zone is restricted to one vehicle type."
                ),
                g
        );


        JComboBox<String> building =
                new JComboBox<>(
                        zones.stream()
                                .map(z ->
                                        z.name() +
                                                " Block"
                                )
                                .toArray(
                                        String[]::new
                                )
                );


        JComboBox<String> type =
                new JComboBox<>(
                        new String[]{
                                "CAR",
                                "BIKE",
                                "EV",
                                "BUS",
                                "SERVICE"
                        }
                );


        JTextField reg =
                new JTextField();


        addField(
                left,
                g,
                2,
                "Destination building",
                building
        );

        addField(
                left,
                g,
                3,
                "Vehicle type",
                type
        );

        addField(
                left,
                g,
                4,
                "Registration",
                reg
        );


        JButton find =
                primary(
                        "✦ FIND BEST SLOT"
                );


        g.gridy = 5;
        g.gridwidth = 2;

        left.add(
                find,
                g
        );


        JTextArea result =
                new JTextArea(
                        9,
                        30
                );

        result.setEditable(false);

        result.setFont(
                new Font(
                        "Consolas",
                        Font.PLAIN,
                        13
                )
        );

        result.setBackground(
                new Color(
                        246,
                        249,
                        253
                )
        );

        result.setForeground(
                TEXT
        );

        result.setBorder(
                new EmptyBorder(
                        12,
                        12,
                        12,
                        12
                )
        );


        g.gridy = 6;

        left.add(
                new JScrollPane(result),
                g
        );


        find.addActionListener(e -> {

            int zi =
                    building.getSelectedIndex();

            String vt =
                    type.getSelectedItem()
                            .toString();


            List<Slot> candidates =
                    new ArrayList<>();


            for (Slot s : slots.values()) {

                if (
                        s.zoneId == zi &&
                                !s.occupied &&
                                compatible(
                                        s,
                                        vt
                                )
                ) {

                    candidates.add(s);
                }
            }


            StringBuilder out =
                    new StringBuilder();


            out.append(
                    "ALLOCATION DECISION\n"
            );

            out.append(
                    "────────────────────────────────\n"
            );

            out.append(
                    "Destination : "
            );

            out.append(
                    zones.get(zi).name
            );

            out.append(
                    " Block\n"
            );


            out.append(
                    "Vehicle     : "
            );

            out.append(vt);

            out.append(
                    " / "
            );

            out.append(
                    reg.getText()
            );

            out.append("\n\n");


            if (candidates.isEmpty()) {

                out.append(
                        "PRIMARY ZONE FULL\n\n"
                );

                out.append(
                        "Fallback policy activated:\n"
                );


                Slot alt =
                        findAlternative(
                                zi,
                                vt
                        );


                if (alt != null) {

                    out.append(
                            "→ Recommend "
                    );

                    out.append(
                            slotName(alt)
                    );

                    out.append("\n");

                    out.append(
                            "→ Reason: nearest available mixed-use zone\n"
                    );

                } else {

                    out.append(
                            "→ No suitable slot across the six zones.\n"
                    );

                    out.append(
                            "→ Add vehicle to Smart Waiting Queue.\n"
                    );
                }

            } else {

                Slot best =
                        candidates.get(0);


                out.append(
                        "RECOMMENDED SLOT\n\n"
                );


                out.append(
                        "  "
                );

                out.append(
                        slotName(best)
                );

                out.append("\n");


                out.append(
                        "  Category  : "
                );

                out.append(
                        best.category
                );

                out.append("\n");


                out.append(
                        "  Score     : "
                );

                out.append(
                        score(
                                best,
                                vt
                        )
                );

                out.append(
                        " / 100\n\n"
                );


                out.append(
                        "WHY THIS SLOT?\n"
                );

                out.append(
                        "✓ Same destination zone\n"
                );


                if (
                        "EV".equals(vt)
                                &&
                                "EV".equals(
                                        best.category
                                )
                ) {

                    out.append(
                            "✓ EV compatibility priority\n"
                    );
                }


                if (
                        "SERVICE".equals(vt)
                                &&
                                "SERVICE".equals(
                                        best.category
                                )
                ) {

                    out.append(
                            "✓ Service access priority\n"
                    );
                }


                out.append(
                        "✓ Currently available\n"
                );

                out.append(
                        "✓ Avoids unnecessary cross-campus movement"
                );
            }


            result.setText(
                    out.toString()
            );
        });


        /* ---------- RIGHT SIDE ---------- */

        JPanel right = card();

        right.setLayout(
                new BorderLayout(
                        8,
                        8
                )
        );


        right.add(
                h(
                        "Allocation Logic",
                        18
                ),
                BorderLayout.NORTH
        );


        JTextArea logic =
                new JTextArea(
                        """
                        1. Identify destination building.
                        2. Map building → preferred parking zone.
                        3. Inspect live slot status.
                        4. Keep every zone mixed-use.
                        5. Give special compatibility priority only when needed.
                        6. If preferred zone is full, search alternative zones.
                        7. If all zones are full, use Smart Waiting Queue.
                        8. Lock selected slot in a transaction.
                        9. Create parking session.
                        10. Release slot automatically at exit.
                        """
                );


        logic.setEditable(false);

        logic.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        logic.setForeground(TEXT);

        logic.setBackground(Color.WHITE);

        logic.setBorder(
                new EmptyBorder(
                        10,
                        5,
                        10,
                        5
                )
        );


        right.add(
                logic,
                BorderLayout.CENTER
        );


        JPanel wrap =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                15,
                                0
                        )
                );

        wrap.setOpaque(false);

        wrap.add(left);
        wrap.add(right);


        p.add(
                wrap,
                BorderLayout.CENTER
        );


        return p;
    }


    void addField(
            JPanel p,
            GridBagConstraints g,
            int y,
            String label,
            JComponent c
    ) {

        g.gridy = y;
        g.gridwidth = 1;
        g.gridx = 0;


        JLabel l =
                new JLabel(label);

        l.setForeground(TEXT);

        l.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );


        p.add(
                l,
                g
        );


        g.gridx = 1;

        p.add(
                c,
                g
        );
    }


    boolean compatible(
            Slot s,
            String type
    ) {

        if ("EV".equals(type)) {

            return
                    "GENERAL".equals(
                            s.category
                    )
                            ||
                            "EV".equals(
                                    s.category
                            );
        }


        if ("SERVICE".equals(type)) {

            return
                    "GENERAL".equals(
                            s.category
                    )
                            ||
                            "SERVICE".equals(
                                    s.category
                            );
        }


        return
                "GENERAL".equals(
                        s.category
                );
    }


    int score(
            Slot s,
            String type
    ) {

        boolean special =
                (
                        "EV".equals(type)
                                &&
                                "EV".equals(
                                        s.category
                                )
                )
                        ||
                        (
                                "SERVICE".equals(type)
                                        &&
                                        "SERVICE".equals(
                                                s.category
                                        )
                        );


        return special
                ? 100
                : 90;
    }


    Slot findAlternative(
            int primary,
            String type
    ) {

        for (
                int d = 1;
                d < zones.size();
                d++
        ) {

            int[] order = {
                    primary + d,
                    primary - d
            };


            for (int zi : order) {

                if (
                        zi >= 0 &&
                                zi < zones.size()
                ) {

                    for (Slot s : slots.values()) {

                        if (
                                s.zoneId == zi &&
                                        !s.occupied &&
                                        compatible(
                                                s,
                                                type
                                        )
                        ) {

                            return s;
                        }
                    }
                }
            }
        }


        return null;
    }


    String slotName(Slot s) {

        return
                zones.get(
                        s.zoneId
                ).name
                        +
                        " Zone • "
                        +
                        s.number;
    }


    /* ========================================================
       RESERVATIONS
       ======================================================== */

    JPanel buildReservations() {

        JPanel p = page();


        p.add(
                sectionHeader(
                        "Reservation Control",
                        "Prevent conflicts before a slot is promised."
                ),
                BorderLayout.NORTH
        );


        JPanel body =
                new JPanel(
                        new BorderLayout(
                                12,
                                12
                        )
                );

        body.setOpaque(false);


        String[] cols = {
                "ID",
                "Vehicle",
                "Slot",
                "Zone",
                "Start",
                "Status"
        };


        Object[][] data =
                new Object[
                        reservations.size()
                        ][6];


        for (
                int i = 0;
                i < reservations.size();
                i++
        ) {

            Reservation r =
                    reservations.get(i);


            data[i] =
                    new Object[]{
                            r.id,
                            r.vehicle,
                            r.slot,
                            r.zone,
                            r.start.format(
                                    DateTimeFormatter.ofPattern(
                                            "dd MMM HH:mm"
                                    )
                            ),
                            r.status
                    };
        }


        JTable table =
                table(
                        data,
                        cols
                );


        body.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );


        JPanel buttons = card();

        buttons.setLayout(
                new FlowLayout(
                        FlowLayout.RIGHT
                )
        );


        JButton add =
                primary(
                        "+ New Reservation"
                );


        buttons.add(add);


        add.addActionListener(
                e ->
                        JOptionPane.showMessageDialog(
                                this,
                                "Reservation workflow:\n\n" +
                                        "Select vehicle → destination → compatible slot → start/end time → conflict check → confirm.",
                                "CampusPulse",
                                JOptionPane.INFORMATION_MESSAGE
                        )
        );


        body.add(
                buttons,
                BorderLayout.SOUTH
        );


        p.add(
                body,
                BorderLayout.CENTER
        );


        return p;
    }


    /* ========================================================
       ENTRY / EXIT
       ======================================================== */

    JPanel buildSessions() {

        JPanel p = page();


        p.add(
                sectionHeader(
                        "Entry / Exit Operations",
                        "Every active vehicle has a traceable parking session."
                ),
                BorderLayout.NORTH
        );


        String[] cols = {
                "Session",
                "Vehicle",
                "Slot",
                "Zone",
                "Entry",
                "State",
                "Fee"
        };


        Object[][] data =
                new Object[
                        sessions.size()
                        ][7];


        for (
                int i = 0;
                i < sessions.size();
                i++
        ) {

            Session s =
                    sessions.get(i);


            data[i] =
                    new Object[]{
                            s.id,
                            vehicleReg(
                                    s.vehicleId
                            ),
                            s.slot,
                            s.zone,
                            s.entry.format(
                                    DateTimeFormatter.ofPattern(
                                            "dd MMM HH:mm"
                                    )
                            ),
                            s.active
                                    ? "ACTIVE"
                                    : "COMPLETED",
                            money(s.fee)
                    };
        }


        JTable table =
                table(
                        data,
                        cols
                );


        p.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );


        JPanel bar = card();

        bar.setLayout(
                new FlowLayout(
                        FlowLayout.RIGHT
                )
        );


        JButton exit =
                primary(
                        "⇥ Process Exit & Calculate Fee"
                );


        bar.add(exit);


        exit.addActionListener(e -> {

            Session activeSession = null;

            for (Session s : sessions) {

                if (s.active) {

                    activeSession = s;
                    break;
                }
            }


            if (activeSession == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "No active parking session found."
                );

                return;
            }


            activeSession.active = false;


            activeSession.fee =
                    calculateFee(
                            Duration.between(
                                    activeSession.entry,
                                    LocalDateTime.now()
                            ).toMinutes()
                    );


            /* Release slot */

            for (Slot slot : slots.values()) {

                if (
                        slot.number.equals(
                                activeSession.slot
                        )
                ) {

                    slot.occupied = false;
                    break;
                }
            }


            JOptionPane.showMessageDialog(
                    this,
                    "Exit recorded.\n\n" +
                            "Parking fee: " +
                            money(
                                    activeSession.fee
                            ) +
                            "\n\nSlot released automatically.",
                    "Payment Required",
                    JOptionPane.INFORMATION_MESSAGE
            );


            showPage(
                    "Entry / Exit",
                    buildSessions()
            );
        });


        p.add(
                bar,
                BorderLayout.SOUTH
        );


        return p;
    }


    double calculateFee(long minutes) {

        if (minutes <= 15) {

            return 0;
        }


        long h =
                (minutes + 59) / 60;


        return
                20 +
                        Math.max(
                                0,
                                h - 1
                        ) * 10;
    }


    String money(double n) {

        return NumberFormat
                .getCurrencyInstance(
                        Locale.forLanguageTag(
                                "en-IN"
                        )
                )
                .format(n);
    }


    String vehicleReg(int id) {

        return
                vehicles.stream()
                        .filter(
                                v -> v.id == id
                        )
                        .map(
                                v -> v.reg
                        )
                        .findFirst()
                        .orElse("-");
    }


    /* ========================================================
       PAYMENTS
       ======================================================== */

    JPanel buildPayments() {

        JPanel p = page();


        p.add(
                sectionHeader(
                        "Payments & Parking Passes",
                        "Cashless-ready fee settlement and pass lifecycle."
                ),
                BorderLayout.NORTH
        );


        JPanel cards =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                12,
                                0
                        )
                );

        cards.setOpaque(false);


        cards.add(
                stat(
                        "TODAY'S REVENUE",
                        "₹18,420",
                        "simulated operational total",
                        GREEN,
                        "₹"
                )
        );


        cards.add(
                stat(
                        "ACTIVE PASSES",
                        "126",
                        "students + staff + visitors",
                        BLUE,
                        "▣"
                )
        );


        cards.add(
                stat(
                        "PENDING",
                        "7",
                        "transactions requiring review",
                        ORANGE,
                        "!"
                )
        );


        p.add(
                cards,
                BorderLayout.CENTER
        );


        JPanel b = card();

        b.setLayout(
                new BorderLayout()
        );


        b.add(
                h(
                        "Payment workflow",
                        17
                ),
                BorderLayout.NORTH
        );


        JLabel workflow =
                new JLabel(
                        "Exit → duration → fee → UPI/Card/Cash → payment record → receipt → session completed"
                );


        workflow.setForeground(TEXT);

        workflow.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );


        b.add(
                workflow,
                BorderLayout.CENTER
        );


        p.add(
                b,
                BorderLayout.SOUTH
        );


        return p;
    }


    /* ========================================================
       VIOLATIONS
       ======================================================== */

    JPanel buildViolations() {

        JPanel p = page();


        p.add(
                sectionHeader(
                        "Violation Management",
                        "Record, track and resolve parking policy exceptions."
                ),
                BorderLayout.NORTH
        );


        String[] cols = {
                "ID",
                "Vehicle",
                "Violation",
                "Penalty",
                "Status"
        };


        Object[][] data =
                new Object[
                        violations.size()
                        ][5];


        for (
                int i = 0;
                i < violations.size();
                i++
        ) {

            Violation v =
                    violations.get(i);


            data[i] =
                    new Object[]{
                            v.id,
                            v.vehicle,
                            v.type,
                            money(v.penalty),
                            v.status
                    };
        }


        p.add(
                new JScrollPane(
                        table(
                                data,
                                cols
                        )
                ),
                BorderLayout.CENTER
        );


        return p;
    }


    /* ========================================================
       REPORTS
       ======================================================== */

    JPanel buildReports() {

        JPanel p = page();


        p.add(
                sectionHeader(
                        "Analytics & Reports",
                        "Operational intelligence for administration and infrastructure planning."
                ),
                BorderLayout.NORTH
        );


        JPanel charts =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                14,
                                0
                        )
                );

        charts.setOpaque(false);


        charts.add(
                new ZoneBarChart(zones)
        );


        charts.add(
                new RevenuePanel()
        );


        p.add(
                charts,
                BorderLayout.CENTER
        );


        JPanel footer = card();

        footer.setLayout(
                new FlowLayout(
                        FlowLayout.LEFT,
                        12,
                        8
                )
        );


        String[] buttons = {
                "Zone-wise occupancy",
                "Vehicle-wise history",
                "Utilization report",
                "Revenue report",
                "Violation summary",
                "Export CSV"
        };


        for (String s : buttons) {

            footer.add(
                    secondary(s)
            );
        }


        p.add(
                footer,
                BorderLayout.SOUTH
        );


        return p;
    }


    /* ========================================================
       SETTINGS
       ======================================================== */

    JPanel buildSettings() {

        JPanel p = page();


        p.add(
                sectionHeader(
                        "System & Database",
                        "JDBC connection, database status and project configuration."
                ),
                BorderLayout.NORTH
        );


        JPanel center =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                14,
                                0
                        )
                );

        center.setOpaque(false);


        /* ---------- DATABASE CARD ---------- */

        JPanel db = card();

        db.setLayout(
                new BoxLayout(
                        db,
                        BoxLayout.Y_AXIS
                )
        );


        db.add(
                h(
                        "MySQL / JDBC",
                        18
                )
        );


        db.add(
                Box.createVerticalStrut(12)
        );


        boolean online =
                DBConnection.isOnline();


        JLabel status =
                new JLabel(
                        online
                                ? "● CONNECTED"
                                : "● DEMO MODE / DATABASE OFFLINE"
                );


        status.setForeground(
                online
                        ? GREEN
                        : ORANGE
        );


        status.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );


        db.add(status);


        db.add(
                Box.createVerticalStrut(12)
        );


        db.add(
                muted(
                        "Database: campuspulse"
                )
        );

        db.add(
                muted(
                        "User: root"
                )
        );

        db.add(
                muted(
                        "Password: configured in DBConnection.java"
                )
        );


        db.add(
                Box.createVerticalStrut(15)
        );


        JButton test =
                secondary(
                        "Test JDBC Connection"
                );


        test.addActionListener(
                e -> {

                    boolean ok =
                            DBConnection.isOnline();


                    JOptionPane.showMessageDialog(
                            this,
                            ok
                                    ? "MySQL connection successful."
                                    : "MySQL unavailable. GUI remains in demo mode."
                    );
                }
        );


        db.add(test);


        /* ---------- TECHNOLOGIES ---------- */

        JPanel tech = card();

        tech.setLayout(
                new BoxLayout(
                        tech,
                        BoxLayout.Y_AXIS
                )
        );


        tech.add(
                h(
                        "Required Java Technologies",
                        18
                )
        );


        String[] t = {

                "✓ Swing controls & custom painting",
                "✓ Layout managers & event handling",
                "✓ Statement — dashboard queries",
                "✓ PreparedStatement — CRUD/search",
                "✓ CallableStatement — smart allocation procedure",
                "✓ Transactions — allocation / payment consistency",
                "✓ SQL validation & exception handling"
        };


        for (String s : t) {

            tech.add(
                    Box.createVerticalStrut(9)
            );


            JLabel label =
                    new JLabel(s);

            label.setForeground(TEXT);

            label.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            13
                    )
            );


            tech.add(label);
        }


        center.add(db);
        center.add(tech);


        p.add(
                center,
                BorderLayout.CENTER
        );


        return p;
    }


    /* ========================================================
       COMMON COMPONENTS
       ======================================================== */

    JPanel sectionHeader(
            String title,
            String sub
    ) {

        JPanel p = card();

        p.setLayout(
                new BorderLayout()
        );


        JPanel x = new JPanel();

        x.setOpaque(false);

        x.setLayout(
                new BoxLayout(
                        x,
                        BoxLayout.Y_AXIS
                )
        );


        x.add(
                h(
                        title,
                        22
                )
        );


        x.add(
                Box.createVerticalStrut(5)
        );


        x.add(
                muted(sub)
        );


        p.add(
                x,
                BorderLayout.WEST
        );


        return p;
    }


    JTable table(
            Object[][] data,
            String[] cols
    ) {

        JTable t =
                new JTable(
                        data,
                        cols
                );


        t.setRowHeight(34);


        t.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );


        t.setForeground(TEXT);

        t.setBackground(Color.WHITE);


        t.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                12
                        )
                );


        t.getTableHeader()
                .setForeground(TEXT);


        t.getTableHeader()
                .setBackground(
                        new Color(
                                238,
                                243,
                                250
                        )
                );


        t.setGridColor(LINE);


        t.setSelectionBackground(
                new Color(
                        220,
                        230,
                        250
                )
        );


        t.setSelectionForeground(TEXT);


        return t;
    }


    /* ========================================================
       REGISTER VEHICLE
       ======================================================== */

    void registerVehicleDialog() {

        JTextField reg =
                new JTextField();


        JComboBox<String> type =
                new JComboBox<>(
                        new String[]{
                                "CAR",
                                "BIKE",
                                "EV",
                                "BUS",
                                "SERVICE"
                        }
                );


        JComboBox<String> role =
                new JComboBox<>(
                        new String[]{
                                "STUDENT",
                                "FACULTY",
                                "STAFF",
                                "VISITOR",
                                "SERVICE"
                        }
                );


        JComboBox<String> zone =
                new JComboBox<>(
                        zones.stream()
                                .map(
                                        z ->
                                                z.name() +
                                                        " Block"
                                )
                                .toArray(
                                        String[]::new
                                )
                );


        JPanel f =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                8,
                                8
                        )
                );


        f.add(
                new JLabel(
                        "Registration"
                )
        );

        f.add(reg);


        f.add(
                new JLabel(
                        "Vehicle type"
                )
        );

        f.add(type);


        f.add(
                new JLabel(
                        "User role"
                )
        );

        f.add(role);


        f.add(
                new JLabel(
                        "Destination"
                )
        );

        f.add(zone);


        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        f,
                        "Register Vehicle & User",
                        JOptionPane.OK_CANCEL_OPTION
                );


        if (
                result ==
                        JOptionPane.OK_OPTION
        ) {


            if (
                    reg.getText()
                            .isBlank()
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "Registration number is required."
                );

                return;
            }


            int id =
                    vehicles.size() + 1;


            String vehicleType =
                    type.getSelectedItem()
                            .toString();


            int zoneId =
                    zone.getSelectedIndex();


            Vehicle v =
                    new Vehicle(
                            id,
                            reg.getText()
                                    .toUpperCase(),
                            vehicleType,
                            role.getSelectedItem()
                                    .toString(),
                            zones.get(zoneId)
                                    .name(),
                            "EV".equals(
                                    vehicleType
                            )
                    );


            vehicles.add(v);


            Slot allocated = null;


            for (Slot s : slots.values()) {

                if (
                        s.zoneId == zoneId &&
                                !s.occupied &&
                                compatible(
                                        s,
                                        vehicleType
                                )
                ) {

                    allocated = s;
                    break;
                }
            }


            if (allocated == null) {

                allocated =
                        findAlternative(
                                zoneId,
                                vehicleType
                        );
            }


            if (allocated != null) {

                allocated.occupied = true;


                sessions.add(
                        new Session(
                                1000 + id,
                                id,
                                allocated.number,
                                zones.get(
                                        allocated.zoneId
                                ).name(),
                                LocalDateTime.now(),
                                true,
                                0
                        )
                );


                JOptionPane.showMessageDialog(
                        this,
                        "Vehicle registered successfully.\n\n" +
                                v.reg() +
                                " is now live in the vehicle registry.\n\n" +
                                "Allocated slot: " +
                                allocated.number +
                                "\n\n" +
                                "Registered vehicles: " +
                                vehicles.size()
                );


            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Vehicle registered successfully.\n\n" +
                                v.reg() +
                                " is in the live vehicle registry, but no compatible slot is currently available.\n\n" +
                                "Registered vehicles: " +
                                vehicles.size()
                );
            }
        }
    }


    /* ========================================================
       DATABASE STATUS
       ======================================================== */

    void updateDbStatus() {

        boolean ok =
                DBConnection.isOnline();


        dbStatus.setText(
                ok
                        ? "● MYSQL CONNECTED"
                        : "● DEMO MODE"
        );


        dbStatus.setForeground(
                ok
                        ? GREEN
                        : ORANGE
        );
    }


    /* ========================================================
       DATA CLASSES
       ======================================================== */

    record Zone(
            int id,
            String name,
            int capacity,
            int occupied
    ) {

        String code() {

            return switch (id) {

                case 0 -> "ENG";
                case 1 -> "AHS";
                case 2 -> "MED";
                case 3 -> "RECT";
                case 4 -> "SCAD";

                default -> "SSPE";
            };
        }


        double rate() {

            return
                    occupied *
                            100.0 /
                            capacity;
        }
    }


    static class Slot {

        int id;
        int zoneId;

        String number;
        String category;

        boolean occupied;


        Slot(
                int i,
                int z,
                String n,
                boolean o,
                String c
        ) {

            id = i;
            zoneId = z;
            number = n;
            occupied = o;
            category = c;
        }
    }


    record Vehicle(
            int id,
            String reg,
            String type,
            String role,
            String zone,
            boolean ev
    ) {
    }


    static class Session {

        int id;
        int vehicleId;

        String slot;
        String zone;

        LocalDateTime entry;

        boolean active;

        double fee;


        Session(
                int i,
                int v,
                String s,
                String z,
                LocalDateTime e,
                boolean a,
                double f
        ) {

            id = i;
            vehicleId = v;
            slot = s;
            zone = z;
            entry = e;
            active = a;
            fee = f;
        }
    }


    record Reservation(
            int id,
            String vehicle,
            String slot,
            String zone,
            LocalDateTime start,
            String status
    ) {
    }


    record Violation(
            int id,
            String vehicle,
            String type,
            double penalty,
            String status
    ) {
    }


    /* ========================================================
       SLOT TILE
       ======================================================== */

    class SlotTile extends JPanel {

        SlotTile(Slot s) {

            setLayout(
                    new BorderLayout(
                            3,
                            3
                    )
            );


            setBorder(
                    new CompoundBorder(
                            new LineBorder(
                                    s.occupied
                                            ? new Color(
                                            245,
                                            170,
                                            178
                                    )
                                            : new Color(
                                            157,
                                            220,
                                            180
                                    ),
                                    1,
                                    true
                            ),
                            new EmptyBorder(
                                    7,
                                    7,
                                    7,
                                    7
                            )
                    )
            );


            setBackground(
                    s.occupied
                            ? new Color(
                            255,
                            243,
                            245
                    )
                            : new Color(
                            238,
                            251,
                            243
                    )
            );


            JLabel n =
                    new JLabel(
                            s.number
                    );


            n.setForeground(TEXT);

            n.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            11
                    )
            );


            JLabel st =
                    new JLabel(
                            s.occupied
                                    ? "OCCUPIED"
                                    : "AVAILABLE",
                            SwingConstants.CENTER
                    );


            st.setForeground(
                    s.occupied
                            ? RED
                            : GREEN
            );


            st.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            10
                    )
            );


            JLabel cat =
                    new JLabel(
                            s.category
                    );


            cat.setForeground(TEXT);


            cat.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            9
                    )
            );


            add(
                    n,
                    BorderLayout.NORTH
            );

            add(
                    st,
                    BorderLayout.CENTER
            );

            add(
                    cat,
                    BorderLayout.SOUTH
            );
        }
    }
}


/* ============================================================
   CAMPUS MAP
   ============================================================ */

class CampusMap extends JPanel {

    final List<CampusPulseFrame.Zone> zones;
    final CampusPulseFrame frame;


    CampusMap(
            List<CampusPulseFrame.Zone> z,
            CampusPulseFrame f
    ) {

        zones = z;
        frame = f;

        setBackground(
                new Color(
                        229,
                        239,
                        231
                )
        );

        setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );
    }


    @Override
    protected void paintComponent(
            Graphics gg
    ) {

        super.paintComponent(gg);


        Graphics2D g =
                (Graphics2D) gg;


        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        g.setColor(
                new Color(
                        195,
                        205,
                        196
                )
        );


        g.fillRoundRect(
                70,
                250,
                getWidth() - 140,
                85,
                42,
                42
        );


        g.fillRoundRect(
                getWidth() / 2 - 50,
                60,
                100,
                getHeight() - 120,
                45,
                45
        );


        g.setColor(
                new Color(
                        142,
                        161,
                        143
                )
        );


        g.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );


        g.drawString(
                "CAMPUS INTERNAL ROAD NETWORK",
                getWidth() / 2 - 105,
                350
        );


        int[][] pos = {
                {70, 80},
                {390, 65},
                {720, 80},
                {70, 430},
                {390, 445},
                {720, 430}
        };


        Color[] cs = {

                CampusPulseFrame.BLUE,
                CampusPulseFrame.CYAN,
                CampusPulseFrame.RED,
                CampusPulseFrame.ORANGE,
                CampusPulseFrame.PURPLE,
                CampusPulseFrame.GREEN
        };


        for (
                int i = 0;
                i < zones.size();
                i++
        ) {

            var z = zones.get(i);

            int x = pos[i][0];
            int y = pos[i][1];


            g.setColor(Color.WHITE);

            g.fillRoundRect(
                    x,
                    y,
                    265,
                    125,
                    18,
                    18
            );


            g.setColor(cs[i]);

            g.fillRoundRect(
                    x,
                    y,
                    9,
                    125,
                    18,
                    18
            );


            g.setColor(
                    CampusPulseFrame.TEXT
            );


            g.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            14
                    )
            );


            g.drawString(
                    z.name().toUpperCase() +
                            " BLOCK",
                    x + 22,
                    y + 28
            );


            g.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            12
                    )
            );


            g.drawString(
                    "Parking Zone • Mixed Use",
                    x + 22,
                    y + 51
            );


            g.drawString(
                    z.occupied() +
                            " occupied / " +
                            z.capacity() +
                            " total",
                    x + 22,
                    y + 71
            );


            g.setColor(
                    new Color(
                            232,
                            236,
                            242
                    )
            );


            g.fillRoundRect(
                    x + 22,
                    y + 88,
                    205,
                    10,
                    5,
                    5
            );


            g.setColor(cs[i]);


            g.fillRoundRect(
                    x + 22,
                    y + 88,
                    (int)
                            (
                                    205 *
                                            z.rate() /
                                            100
                            ),
                    10,
                    5,
                    5
            );


            g.setColor(
                    Color.DARK_GRAY
            );


            g.fillOval(
                    x + 224,
                    y + 17,
                    18,
                    18
            );


            g.setColor(Color.WHITE);

            g.drawString(
                    "C",
                    x + 229,
                    y + 30
            );
        }


        g.setColor(
                CampusPulseFrame.TEXT
        );


        g.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );


        g.drawString(
                "● CCTV nodes",
                35,
                getHeight() - 18
        );


        g.drawString(
                "● Destination-aware zone mapping",
                160,
                getHeight() - 18
        );
    }
}


/* ============================================================
   CCTV CAMERA CARD
   ============================================================ */

class CameraCard extends JPanel {

    CameraCard(
            CampusPulseFrame.Slot slot,
            CampusPulseFrame frame
    ) {

        setLayout(
                new BorderLayout()
        );


        boolean occ =
                slot.occupied;


        Color accent =
                occ
                        ? CampusPulseFrame.RED
                        : CampusPulseFrame.GREEN;


        setBackground(Color.WHITE);


        setBorder(
                new LineBorder(
                        accent,
                        2,
                        true
                )
        );


        JLabel h =
                new JLabel(
                        "CAM-" +
                                String.format(
                                        "%03d",
                                        slot.id
                                ) +
                                "  •  " +
                                slot.number +
                                "  •  " +
                                frame.zones
                                        .get(
                                                slot.zoneId
                                        )
                                        .name()
                                        .toUpperCase()
                );


        h.setForeground(
                CampusPulseFrame.TEXT
        );


        h.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );


        h.setBorder(
                new EmptyBorder(
                        5,
                        6,
                        5,
                        6
                )
        );


        add(
                h,
                BorderLayout.NORTH
        );


        add(
                new CameraView(
                        slot,
                        frame
                ),
                BorderLayout.CENTER
        );


        JLabel f =
                new JLabel(
                        occ
                                ? "  ● OCCUPIED / VEHICLE PRESENT"
                                : "  ● AVAILABLE / NO VEHICLE"
                );


        f.setForeground(
                occ
                        ? CampusPulseFrame.RED
                        : CampusPulseFrame.GREEN
        );


        f.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        10
                )
        );


        add(
                f,
                BorderLayout.SOUTH
        );
    }
}


/* ============================================================
   CAMERA VIEW
   ============================================================ */

class CameraView extends JPanel {

    final CampusPulseFrame.Slot slot;
    final CampusPulseFrame frame;


    CameraView(
            CampusPulseFrame.Slot s,
            CampusPulseFrame f
    ) {

        slot = s;
        frame = f;


        setPreferredSize(
                new Dimension(
                        250,
                        145
                )
        );


        setBackground(
                new Color(
                        242,
                        246,
                        250
                )
        );
    }


    @Override
    protected void paintComponent(
            Graphics gg
    ) {

        super.paintComponent(gg);


        Graphics2D g =
                (Graphics2D) gg;


        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        g.setColor(
                new Color(
                        220,
                        226,
                        234
                )
        );


        g.fillRoundRect(
                10,
                38,
                getWidth() - 20,
                getHeight() - 50,
                12,
                12
        );


        g.setColor(
                new Color(
                        180,
                        188,
                        198
                )
        );


        for (
                int x = 20;
                x < getWidth() - 20;
                x += 38
        ) {

            g.fillRoundRect(
                    x,
                    60,
                    25,
                    60,
                    4,
                    4
            );
        }


        boolean hasVehicle = false;

        String vehicleText =
                "NO REGISTERED VEHICLE";


        CampusPulseFrame.Vehicle liveVehicle =
                null;


        for (
                CampusPulseFrame.Session session :
                frame.sessions
        ) {

            if (
                    session.active
                            &&
                            slot.number.equals(
                                    session.slot
                            )
            ) {

                for (
                        CampusPulseFrame.Vehicle v :
                        frame.vehicles
                ) {

                    if (
                            v.id() ==
                                    session.vehicleId
                    ) {

                        liveVehicle = v;
                        break;
                    }
                }

                break;
            }
        }


        if (liveVehicle != null) {

            hasVehicle = true;

            vehicleText =
                    liveVehicle.reg();
        }


        if (hasVehicle) {

            g.setColor(
                    CampusPulseFrame.BLUE
            );


            g.fillRoundRect(
                    getWidth() / 2 - 22,
                    72,
                    44,
                    55,
                    8,
                    8
            );


            g.setColor(
                    CampusPulseFrame.CYAN
            );


            g.fillRoundRect(
                    getWidth() / 2 - 16,
                    66,
                    32,
                    12,
                    5,
                    5
            );


            g.setColor(Color.WHITE);


            g.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            9
                    )
            );


            g.drawString(
                    "LIVE",
                    getWidth() / 2 - 12,
                    101
            );


            g.setColor(
                    Color.BLACK
            );


            g.setFont(
                    new Font(
                            "Consolas",
                            Font.BOLD,
                            10
                    )
            );


            g.drawString(
                    vehicleText,
                    12,
                    getHeight() - 12
            );


        } else if (slot.occupied) {

            g.setColor(
                    new Color(
                            235,
                            85,
                            100
                    )
            );


            g.fillOval(
                    getWidth() / 2 - 12,
                    78,
                    24,
                    24
            );


            g.setColor(Color.BLACK);


            g.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            10
                    )
            );


            g.drawString(
                    "OCCUPIED SLOT",
                    12,
                    getHeight() - 12
            );


        } else {

            g.setColor(
                    new Color(
                            55,
                            190,
                            120
                    )
            );


            g.drawRoundRect(
                    getWidth() / 2 - 20,
                    75,
                    40,
                    42,
                    7,
                    3
            );


            g.setColor(Color.BLACK);


            g.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            10
                    )
            );


            g.drawString(
                    "SLOT CLEAR",
                    12,
                    getHeight() - 12
            );
        }


        g.setColor(Color.BLACK);


        g.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        10
                )
        );


        g.drawString(
                "LIVE CAMERA • " +
                        slot.number,
                10,
                20
        );
    }
}


/* ============================================================
   ZONE BAR CHART
   ============================================================ */

class ZoneBarChart extends JPanel {

    final List<CampusPulseFrame.Zone> zones;


    ZoneBarChart(
            List<CampusPulseFrame.Zone> z
    ) {

        zones = z;


        setBackground(Color.WHITE);


        setBorder(
                new CompoundBorder(
                        new LineBorder(
                                CampusPulseFrame.LINE,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                18,
                                18,
                                18,
                                18
                        )
                )
        );


        setPreferredSize(
                new Dimension(
                        500,
                        390
                )
        );
    }


    @Override
    protected void paintComponent(
            Graphics gg
    ) {

        super.paintComponent(gg);


        Graphics2D g =
                (Graphics2D) gg;


        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        g.setColor(
                CampusPulseFrame.TEXT
        );


        g.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );


        g.drawString(
                "Zone Utilization",
                18,
                30
        );


        int x = 35;

        int base =
                getHeight() - 55;


        for (
                var z : zones
        ) {

            int bh =
                    (int)
                            (
                                    (
                                            getHeight() - 115
                                    )
                                            *
                                            z.rate()
                                            /
                                            100
                            );


            g.setColor(
                    z.rate() > 80
                            ? CampusPulseFrame.RED
                            : z.rate() > 65
                              ? CampusPulseFrame.ORANGE
                              : CampusPulseFrame.GREEN
            );


            g.fillRoundRect(
                    x,
                    base - bh,
                    42,
                    bh,
                    8,
                    8
            );


            g.setColor(
                    CampusPulseFrame.MUTED
            );


            g.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            10
                    )
            );


            String name =
                    z.name().substring(
                            0,
                            Math.min(
                                    6,
                                    z.name().length()
                            )
                    );


            g.drawString(
                    name,
                    x - 2,
                    base + 18
            );


            g.drawString(
                    String.format(
                            "%.0f%%",
                            z.rate()
                    ),
                    x + 7,
                    base - bh - 7
            );


            x += 70;
        }
    }
}


/* ============================================================
   REVENUE PANEL
   ============================================================ */

class RevenuePanel extends JPanel {

    RevenuePanel() {

        setBackground(Color.WHITE);


        setBorder(
                new CompoundBorder(
                        new LineBorder(
                                CampusPulseFrame.LINE,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                18,
                                18,
                                18,
                                18
                        )
                )
        );


        setPreferredSize(
                new Dimension(
                        500,
                        390
                )
        );
    }


    @Override
    protected void paintComponent(
            Graphics gg
    ) {

        super.paintComponent(gg);


        Graphics2D g =
                (Graphics2D) gg;


        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        g.setColor(
                CampusPulseFrame.TEXT
        );


        g.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );


        g.drawString(
                "Revenue Overview",
                18,
                30
        );


        /* Revenue value */

        g.setColor(
                CampusPulseFrame.GREEN
        );


        g.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        32
                )
        );


        g.drawString(
                "₹18,420",
                25,
                85
        );


        g.setColor(
                CampusPulseFrame.MUTED
        );


        g.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );


        g.drawString(
                "Today's simulated parking revenue",
                25,
                108
        );


        /* Chart */

        int left = 40;

        int bottom =
                getHeight() - 55;


        int chartHeight = 170;


        int[] values = {
                35,
                55,
                45,
                75,
                62,
                90,
                80
        };


        String[] days = {
                "Mon",
                "Tue",
                "Wed",
                "Thu",
                "Fri",
                "Sat",
                "Sun"
        };


        g.setColor(
                new Color(
                        235,
                        239,
                        245
                )
        );


        for (
                int i = 0;
                i < 5;
                i++
        ) {

            int y =
                    bottom -
                            i *
                                    (
                                            chartHeight /
                                                    4
                                    );


            g.drawLine(
                    left,
                    y,
                    getWidth() - 25,
                    y
            );
        }


        int previousX = -1;
        int previousY = -1;


        for (
                int i = 0;
                i < values.length;
                i++
        ) {

            int x =
                    left +
                            i *
                                    58;


            int y =
                    bottom -
                            (
                                    values[i] *
                                            chartHeight /
                                            100
                            );


            if (previousX != -1) {

                g.setColor(
                        CampusPulseFrame.BLUE
                );


                g.setStroke(
                        new BasicStroke(
                                3f
                        )
                );


                g.drawLine(
                        previousX,
                        previousY,
                        x,
                        y
                );
            }


            g.setColor(
                    CampusPulseFrame.BLUE
            );


            g.fillOval(
                    x - 5,
                    y - 5,
                    10,
                    10
            );


            g.setColor(
                    CampusPulseFrame.MUTED
            );


            g.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            10
                    )
            );


            g.drawString(
                    days[i],
                    x - 10,
                    bottom + 20
            );


            previousX = x;
            previousY = y;
        }


        g.setColor(
                CampusPulseFrame.MUTED
        );


        g.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );


        g.drawString(
                "Weekly revenue trend",
                25,
                getHeight() - 15
        );
    }
}
