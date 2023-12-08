package org.example.ui.dialog;

import lombok.RequiredArgsConstructor;
import org.example.model.Group;

import javax.swing.*;
import java.util.Optional;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.example.utils.Constants.DATA_ENTERED_INCORRECTLY;
import static org.example.utils.Constants.ERROR;


@RequiredArgsConstructor
public class GroupDialog {

    private final JFrame frame;

    // диалог для добавления группы
    public Optional<Group> create() {
        return getModifiedGroup(null, "Добавление группы");
    }

    // диалог для реактирования группы
    public Optional<Group> edit(Group oldGroup) {
        return getModifiedGroup(oldGroup, "Редактирование группы №" + oldGroup.getNumber());
    }

    private Optional<Group> getModifiedGroup(Group oldGroup, String title) {
        // создаем компоненты ввода внутри панели
        var layout = new SpringLayout();
        var panel = new JPanel(layout);
        var label = new JLabel("Наименование");
        panel.add(label);
        var name = new JTextField(12);
        panel.add(name);
        layout.putConstraint(SpringLayout.WEST, name, 12, SpringLayout.EAST, label);

        // вводим данные в поля ввода, если редактирование
        if (oldGroup != null) {
            name.setText(oldGroup.getName());
        }

        // отображаем диалог ввода данных
        var result = showConfirmDialog(frame, panel, title, OK_CANCEL_OPTION, PLAIN_MESSAGE);

        // обрабатываем результат
        if (result == 0) {
            if (!name.getText().isBlank()) {
                var value = oldGroup == null ? new Group(name.getText()) : oldGroup.setName(name.getText());
                return Optional.of(value);
            } else {
                showMessageDialog(frame, DATA_ENTERED_INCORRECTLY, ERROR, ERROR_MESSAGE);
            }
        }

        return Optional.empty();
    }
}
