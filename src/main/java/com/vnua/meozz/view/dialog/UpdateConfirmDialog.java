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
import com.gluonhq.charm.glisten.control.Rating;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UpdateConfirmDialog extends Dialog {

    @SuppressWarnings("unchecked")
	public UpdateConfirmDialog(String title) {
        setTitleText("Thông báo xác nhận");
        VBox content = new VBox();
        Label sessionTitleLabel = new Label(title);
        sessionTitleLabel.setStyle("-fx-font-family: 'Grandstander';");
        sessionTitleLabel.setPrefHeight(40);
        StackPane stackPane = new StackPane();

        content.getChildren().addAll(sessionTitleLabel, stackPane);

        setContent(content);

        Button btnClose = new Button("Đóng");
        btnClose.setStyle("-fx-font-family: 'Grandstander';");
        getButtons().addAll(btnClose);

        btnClose.setOnAction(event -> {
            hide();
        });

        content.getStyleClass().add("content");
        sessionTitleLabel.getStyleClass().add("session-title");

    }


}
