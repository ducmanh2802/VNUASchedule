/**
 * Copyright (c) 2016, Gluon Software
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse
 *    or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.vnua.meozz.view.dialog;

import com.gluonhq.charm.glisten.control.Dialog;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.function.Supplier;

public class SessionConflictDialog extends Dialog {

    public SessionConflictDialog( ){
        this.rootNode.getStyleClass().add("session-conflict-dialog");
        
        setTitle(new Label("OTN.CONFLICT.SCHEDULE_CONFLICT"));

        RadioButton rbScheduled = new RadioButton("OTN.CONFLICT.CURRENTLY_SCHEDULED");
        RadioButton rbProposed = new RadioButton("OTN.CONFLICT.PROPOSED");

        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(rbScheduled, rbProposed);
        group.selectToggle(rbScheduled);
        
        final VBox vBox = new VBox(
                new Label("OTN.CONFLICT.QUESTION"));
        vBox.getStyleClass().add("content");

        setContent(vBox);

        getButtons().addAll();
    }

//    private Button createButton(String title, Supplier<Session> resultSupplier ) {
//        final Button button = new Button(title.toUpperCase());
//        button.setOnAction(e -> {
//            setResult(resultSupplier.get());
//            this.hide();
//        });
//        return button;
//    }

//    private VBox sessionPane(Session session, RadioButton button) {
//        final VBox vBox = new VBox(button,
//                sessionTitle(session, button),
//                sessionInfo(session, button));
//        vBox.getStyleClass().add("session-pane");
//        return vBox;
//    }

//    private Label sessionTitle(Session session, RadioButton button) {
//        Label title = new Label(session.getTitle());
//        title.setOnMouseClicked( e -> button.fire());
//        title.getStyleClass().add("title");
//        return title;
//    }

//    private Label sessionInfo(Session session, RadioButton button) {
//        Label info = new Label(sessionVisuals.formatMultilineInfo(session));
//        info.setOnMouseClicked( e -> button.fire());
//        return info;
//    }


}