package Crud;
import java.awt.*;
import java.awt.event.*;
public class StudentCRUD extends Frame {

    Label lblTitle, lblId, lblName, lblDept, lblMark;
    TextField txtId, txtName, txtDept, txtMark;
    Button btnAdd, btnSearch, btnUpdate, btnDelete, btnClear, btnView;
    TextArea txtArea;

    public StudentCRUD() {

        setTitle("Student Management - CRUD Application");
        setSize(700, 500);
        setLayout(new BorderLayout(10, 10));
        // TITLE
        lblTitle = new Label("STUDENT MANAGEMENT SYSTEM", Label.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

        // FORM
        Panel formPanel = new Panel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));

        lblId = new Label("Student ID");
        txtId = new TextField();

        lblName = new Label("Name");
        txtName = new TextField();

        lblDept = new Label("Department");
        txtDept = new TextField();

        lblMark = new Label("Mark");
        txtMark = new TextField();

        formPanel.add(lblId);
        formPanel.add(txtId);

        formPanel.add(lblName);
        formPanel.add(txtName);

        formPanel.add(lblDept);
        formPanel.add(txtDept);

        formPanel.add(lblMark);
        formPanel.add(txtMark);

        // BUTTONS
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new FlowLayout());

        btnAdd = new Button("Add");
        btnSearch = new Button("Search");
        btnUpdate = new Button("Update");
        btnDelete = new Button("Delete");
        btnClear = new Button("Clear");
        btnView = new Button("View");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnView);

        // CENTER PANEL
        Panel centerPanel = new Panel();
        centerPanel.setLayout(new BorderLayout(10, 10));

        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // TEXT AREA
        txtArea = new TextArea();
        txtArea.setEditable(false);
        add(txtArea, BorderLayout.SOUTH);

        // ADD BUTTON
        btnAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                try {

                    int id = Integer.parseInt(txtId.getText());
                    String name = txtName.getText();
                    String dept = txtDept.getText();
                    int mark = Integer.parseInt(txtMark.getText());

                    Student s = new Student(id, name, dept, mark);

                    StudentDAO dao = new StudentDAO();

                    boolean result = dao.insertStudent(s);

                    if (result) {
                        txtArea.setText("Student added successfully!");
                    } else {
                        txtArea.setText("Failed to add student.");
                    }

                } catch (NumberFormatException ex) {

                    txtArea.setText("ID and Mark must be numbers.");

                } catch (Exception ex) {

                    txtArea.setText("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // CLEAR BUTTON
        btnClear.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                txtId.setText("");
                txtName.setText("");
                txtDept.setText("");
                txtMark.setText("");
                txtArea.setText("");
            }
        });

        // WINDOW CLOSE
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setVisible(true);
    }
    public static void main(String[] args) {
        new StudentCRUD();
    }
}