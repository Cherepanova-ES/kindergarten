package org.example.ui.dialog;

import lombok.RequiredArgsConstructor;
import org.example.model.Child;
import org.example.model.Gender;
import org.example.model.Group;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Optional;

import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showConfirmDialog;
import static org.example.utils.Constants.DATA_ENTERED_INCORRECTLY;
import static org.example.utils.Constants.ERROR;

@RequiredArgsConstructor
public class ChildDialog {

    private final JFrame frame;
    private final JList<Group> groupList;

    // диалог для добавления ребенка
    public Optional<Child> create() {
        return getNewChild(null, "Добавление ребенка");
    }

    // диалог для реактирования ребенка
    public Optional<Child> edit(Child oldChild) {
        return getNewChild(oldChild, "Редактирование ребенка");
    }

    private Optional<Child> getNewChild(Child oldChild, String title) {
        // создаем компоненты ввода внутри панели
        var panel = new JPanel(new BorderLayout(10, 10));
        var labels = new JPanel(new GridLayout(0, 1, 5, 5));
        labels.add(new JLabel("Группа", SwingConstants.RIGHT));
        labels.add(new JLabel("ФИО", SwingConstants.RIGHT));
        labels.add(new JLabel("Пол", SwingConstants.RIGHT));
        labels.add(new JLabel("Возраст", SwingConstants.RIGHT));
        panel.add(labels, BorderLayout.LINE_START);

        var controls = new JPanel(new GridLayout(0, 1, 5, 5));

        var group = new JComboBox<>(((DefaultListModel<Group>) groupList.getModel()).toArray());
        var fullName = new JTextField();
        var gender = new JComboBox<>(Gender.values());
        var formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        formatter.setValueClass(Integer.class);
        var age = new JFormattedTextField(formatter);

        controls.add(group);
        controls.add(fullName);
        controls.add(gender);
        controls.add(age);
        panel.add(controls, BorderLayout.CENTER);

        Optional.ofNullable(groupList.getSelectedValue()).ifPresent(group::setSelectedItem);

        // вводим данные в поля ввода, если редактирование
        if (oldChild != null) {
            fullName.setText(oldChild.getFullName());
            gender.setSelectedItem(oldChild.getGender());
            age.setValue(oldChild.getAge());
        }

        // отображаем диалог ввода данных
        var result = showConfirmDialog(frame, panel, title, OK_CANCEL_OPTION, PLAIN_MESSAGE);

        if (result == 0) {
            if (!fullName.getText().isBlank() && age.getValue() != null) {
                var value = oldChild == null
                        ? new Child(
                            ((Group) group.getSelectedItem()).getNumber(),
                            fullName.getText(),
                            (Gender) gender.getSelectedItem(),
                            ((Integer) age.getValue()))
                        : oldChild
                            .setGroupNumber(((Group) group.getSelectedItem()).getNumber())
                            .setFullName(fullName.getText())
                            .setGender((Gender) gender.getSelectedItem())
                            .setAge((Integer) age.getValue());
                return Optional.of(value);
            } else {
                JOptionPane.showMessageDialog(frame, DATA_ENTERED_INCORRECTLY, ERROR, JOptionPane.ERROR_MESSAGE);
            }
        }

        return Optional.empty();
    }
}
