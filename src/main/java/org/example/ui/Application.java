package org.example.ui;

import org.example.db.ChildDbClient;
import org.example.db.DbClient;
import org.example.db.GroupDbClient;
import org.example.model.Child;
import org.example.model.Group;
import org.example.ui.dialog.ChildDialog;
import org.example.ui.dialog.GroupDialog;
import org.example.ui.popup.CustomPopupMenu;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Application {

    private final JFrame frame;
    private final JList<Group> groupList;
    private final JList<Child> childList;
    private final JLabel counterLabel;
    private final GroupDialog groupDialog;
    private final ChildDialog childDialog;

    public Application() {
        groupList = createGroupList(); // создаем компонент для списка групп
        childList = createChildList(); // создаем компонент для списка детей
        counterLabel = new JLabel("", SwingConstants.RIGHT); // создаем счетчик групп/детей

        // создаем основной frame
        frame = new JFrame("Детский сад");
        frame.setSize(500, 500);
        frame.setIconImage(getIcon("/ico/home.png").getImage());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(createContentPanel());
        frame.setLocationRelativeTo(null); // помещаем в центр экрана
        frame.setVisible(true);

        groupDialog = new GroupDialog(frame);               // создаем диалог для работы с группами
        childDialog = new ChildDialog(frame, groupList);    // создаем диалог для работы с детьми
        refreshUI();
    }

    // метод обнновления UI
    private void refreshUI() {
        refreshGroupList(); // обновляем список групп

        // выделяем первую группу если она есть
        if (groupList.getModel().getSize() > 0) {
            groupList.setSelectedIndex(0);
        }
    }

    // метод создания основной панели контента
    private JPanel createContentPanel() {
        var p = new JPanel();
        var padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        p.setBorder(padding);

        var layout = new BorderLayout(5, 5);
        p.setLayout(layout);
        p.add(createButtonPanel(), BorderLayout.PAGE_START);
        p.add(new JScrollPane(groupList), BorderLayout.WEST);
        p.add(new JScrollPane(childList), BorderLayout.CENTER);
        p.add(createInfoPanel(), BorderLayout.SOUTH);
        return p;
    }

    // метод создания верхней панели кнопок
    public JPanel createButtonPanel() {
        var p = new JPanel(new BorderLayout(10, 0));
        var addGroupBtn = new JButton("Добавить группу", getIcon("/ico/group.png"));
        addGroupBtn.addActionListener(e -> createGroup());
        var addChildBtn = new JButton("Добавить ребенка", getIcon("/ico/male.png"));
        addChildBtn.addActionListener(e -> createChild());
        p.add(addGroupBtn, BorderLayout.WEST);
        p.add(addChildBtn, BorderLayout.EAST);
        return p;
    }

    // метод создания нижней информационной панели
    public JPanel createInfoPanel() {
        var p = new JPanel(new BorderLayout(10, 0));
        var testDataBtn = new JButton(getIcon("/ico/redo.png"));
        testDataBtn.setToolTipText("Добавить тестовые данные");
        testDataBtn.addActionListener(e -> loadTestData());
        p.add(testDataBtn, BorderLayout.WEST);
        p.add(counterLabel, BorderLayout.EAST);
        return p;
    }

    // метод создания списка групп
    public JList<Group> createGroupList() {
        var jList = new JList<Group>();
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setComponentPopupMenu(createGroupListPopupMenu(jList));
        jList.setPreferredSize(new Dimension(150, 10));
        jList.addListSelectionListener(e -> refreshChildList());
        return jList;
    }

    // метод создания списка детей
    public JList<Child> createChildList() {
        var jList = new JList<Child>();
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setComponentPopupMenu(createChildListPopupMenu(jList));
        return jList;
    }

    // метод создания всплывающего меню для списка групп
    private JPopupMenu createGroupListPopupMenu(JList<Group> groupList) {
        return CustomPopupMenu.create(groupList,
                e -> editGroup(),       // обработчик редактирования группы
                e -> deleteGroup());    // обработчик удаления группы
    }

    // метод создания всплывающего меню для списка детей
    private JPopupMenu createChildListPopupMenu(JList<Child> childList) {
        return CustomPopupMenu.create(childList,
                e -> editChild(),       // обработчик редактирования ребенка
                e -> deleteChild());    // обработчик удаления ребенка
    }

    // метод обновления списка групп
    public void refreshGroupList() {
        try {
            var model = new DefaultListModel<Group>();
            model.addAll(GroupDbClient.findAll());
            groupList.setModel(model);
            refreshChildList();
        } catch (Exception e) {
            showError(e);
        }
    }

    // метод обновления списка детей
    public void refreshChildList() {
        try {
            var group = groupList.getSelectedValue();
            var model = new DefaultListModel<Child>();

            if (group != null) {
                model.addAll(ChildDbClient.findAllByGroupId(group.getNumber()));
            }

            childList.setModel(model);
            refreshCounterLabel();
        } catch (Exception e) {
            showError(e);
        }
    }

    // метод обновления счетчика групп/детей
    private void refreshCounterLabel() {
        ChildDbClient.count().ifPresent(childCounter -> {
            var text = String.format("групп: %d детей: %d", groupList.getModel().getSize(), childCounter);
            counterLabel.setText(text);
        });
    }

    // метод создания таблиц и добавления тестовых данных
    private void loadTestData() {
        try {
            DbClient.loadTestData();
            refreshUI();
        } catch (Exception e) {
            showError(e);
        }
    }

    // метод отображения ошибок работы с БД
    private void showError(Exception ex) {
        showMessageDialog(frame, ex.getMessage(), "Ошибка БД", ERROR_MESSAGE);
    }

    // метод добавления группы
    private void createGroup() {
        groupDialog.create().ifPresent(group -> {
            try {
                GroupDbClient.create(group);
                refreshGroupList();
                groupList.setSelectedIndex(groupList.getModel().getSize() - 1);
            } catch (Exception ex) {
                showError(ex);
            }
        });
    }

    // метод редактирования группы
    private void editGroup() {
        Optional.ofNullable(groupList.getSelectedValue())
                .flatMap(groupDialog::edit)
                .ifPresent(group -> {
                    try {
                        GroupDbClient.update(group);
                        groupList.updateUI();
                    } catch (Exception ex) {
                        showError(ex);
                    }
                });
    }

    // метод удаления группы
    private void deleteGroup() {
        Optional.ofNullable(groupList.getSelectedValue())
                .ifPresent(group -> {
                    try {
                        GroupDbClient.delete(group);
                        refreshGroupList();
                    } catch (Exception ex) {
                        showError(ex);
                    }
                });
    }

    // метод добавления ребенка
    private void createChild() {
        childDialog.create().ifPresent(child -> {
            try {
                ChildDbClient.create(child);
                var group = getChildGroup(child);
                groupList.setSelectedValue(group, true);
                refreshChildList();
                childList.setSelectedIndex(childList.getModel().getSize() - 1);
            } catch (Exception ex) {
                showError(ex);
            }
        });
    }

    // метод редактирования ребенка
    private void editChild() {
        Optional.ofNullable(childList.getSelectedValue())
                .flatMap(childDialog::edit)
                .ifPresent(child -> {
                    try {
                        ChildDbClient.update(child);
                        var group = getChildGroup(child);
                        groupList.setSelectedValue(group, true);
                        childList.setSelectedValue(child, true);
                    } catch (Exception ex) {
                        showError(ex);
                    }
                });
    }

    // метод удаления ребенка
    private void deleteChild() {
        Optional.ofNullable(childList.getSelectedValue())
                .ifPresent(child -> {
                    try {
                        ChildDbClient.delete(child);
                        refreshChildList();
                    } catch (Exception ex) {
                        showError(ex);
                    }
                });
    }

    // метод получения группы ребенка
    private Group getChildGroup(Child child) {
        var model = (DefaultListModel<Group>) groupList.getModel();
        return Arrays.stream(model.toArray())
                .map(Group.class::cast)
                .filter(group -> group.getNumber() == child.getGroupNumber())
                .findFirst()
                .orElseThrow();
    }

    private ImageIcon getIcon(String path) {
        var resource = Application.class.getResource(path);
        return new ImageIcon(resource);
    }
}
