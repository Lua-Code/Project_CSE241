package client;

import Classes.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import javafx.scene.image.Image;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.control.Label;


public class GUI extends Application {

    ArrayList<String> userData = new ArrayList<>();
    ArrayList<Category> categories = new ArrayList<>();
    ArrayList<String> userProducts = new ArrayList<>();
    ArrayList<Integer> prices = new ArrayList<>();
    private LocalClientHandler client;
    private User currentUser = null;

    static final String btColor = "#246891";
    static final String skyWhip = "#f0f8ff";
    static final String prussianBlue = "#003049";
    static final String airBlue = "#669bbc";

    static final double widthDif = 15;
    static final double heightDif = 37.5;

    static final Font biggerFont = Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 21);
    static final Font generalFont = Font.font("Verdana", 25);
    static final Font titleFont = Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 22);
    static final Font smallerFont = Font.font("Verdana", 18);
    static final Font smallerFont2 = Font.font("Verdana", 15);
    static final Font smallestFont = Font.font("Verdana", 12);
    static final Font productNameFont = Font.font("Verdana", FontWeight.BOLD, 14);
    static final Font productPriceFont = Font.font("Verdana", FontWeight.NORMAL, 12);
    static final Font productDescFont = Font.font("Verdana", FontPosture.ITALIC, 12);
    static final Font categoryFont = Font.font("Verdana", FontWeight.BOLD, 16);

    Image icon = new Image("C:\\Users\\user\\IdeaProjects\\Client_maven\\src\\main\\java\\Resources\\Preview (1).png");
    Image cardP = new Image("C:\\Users\\user\\IdeaProjects\\Client_maven\\src\\main\\java\\Resources\\cc.png");
    Image pfp = new Image("C:\\Users\\user\\IdeaProjects\\Client_maven\\src\\main\\java\\Resources\\default pfp.jpg");
    ImageView pfp2 = new ImageView(pfp);
    ImageView icon2 = new ImageView(icon);
    ImageView card = new ImageView(cardP);

    public void start(Stage primaryStage) throws IOException {
        client = new LocalClientHandler("localhost",11111);
        client.connect();

        Scene loginMenu = createLogin(primaryStage, 1200, 600);
        // Scene mainMenu = createMain(primaryStage, 1200, 600);
        // Scene registerMenu = createRegister(primaryStage);
        // Scene profileMenu = createProfile(primaryStage);
        // Scene purchaseMenu = createPurchase(primaryStage);
        // Scene cartMenu = createCart(primaryStage, 1200, 600);

        primaryStage.setTitle("The Corner Shop");
        primaryStage.setOnCloseRequest(event -> {
            client.disconnect();
            System.out.println("Connection closed.");
        });
        primaryStage.show();

    }

    // ************************** CART MENU **************************

    private Scene createCart(Stage primaryStage, double width, double height) throws IOException {
        BorderPane menu = new BorderPane();
        GridPane top = createMainTop(primaryStage);
        VBox left = createSide(primaryStage);
        BorderPane center = createCartCenter(primaryStage);

        Scene cartMenu = new Scene(menu, width, height);

        menu.setTop(top);
        menu.setLeft(left);
        menu.setCenter(center);

        primaryStage.setScene(cartMenu);
        return cartMenu;
    }

    private BorderPane createCartCenter(Stage primaryStage) throws IOException {
        BorderPane center = new BorderPane();
        VBox content = new VBox();
        HBox header = new HBox();
        ScrollPane body = new ScrollPane();

        center.setStyle("-fx-border-color: #669bbc; -fx-background-color: #f0f8ff;");

        Label head = new Label("Your Cart");
        head.setFont(generalFont);
        head.setStyle("-fx-text-fill: #003049; -fx-font-weight: bold;");
        header.getChildren().add(head);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);

        content.setSpacing(10);
        content.setPadding(new Insets(20));

        Response response = client.getCart();
        Map<String, Object> dataMap = response.getData();  // Assuming this returns a Map<String, Object>
        Cart currentCart = client.getMapper().convertValue(dataMap.get("newCart"), Cart.class);
        Map<Product, Integer> cartItems = currentCart.getcartItems();
        double total = currentCart.getTotal();
        int x = 1;

        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            HBox row = createCartRow(entry.getKey().getName(), entry.getValue(),entry.getKey().getPrice(), x, primaryStage);
            content.getChildren().add(row);
        }


        Label totalLabel = new Label("Total: $" + total);
        totalLabel.setFont(smallerFont);
        totalLabel.setStyle("-fx-text-fill: #003049; -fx-font-weight: bold;");

        Button proceedButton = new Button("Proceed to Purchase");
        proceedButton.setFont(smallerFont);
        proceedButton.setStyle("-fx-background-color: #003049; -fx-text-fill: white;");
        proceedButton.setOnAction(e -> {
            if (!currentCart.isEmpty()) {
                try {
                    Response purchaseResponse = client.requestPurchase(client.getCurrentUser().getUsername());
                    if(purchaseResponse.getStatus().equals("PURCHASE_SUCCESSFUL")){
                        client.updateCurrentUser();
                        Scene updatedCartMenu;
                        updatedCartMenu = createCart(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
                        primaryStage.setScene(updatedCartMenu);
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        proceedButton.setOnMouseExited(e -> {
            proceedButton.setCursor(Cursor.DEFAULT);
        });

        proceedButton.setOnMouseEntered(e -> {
            proceedButton.setCursor(Cursor.HAND);
        });

        VBox footer = new VBox(totalLabel, proceedButton);
        footer.setSpacing(10);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20));

        body.setContent(content);
        body.setFitToWidth(true);

        center.setTop(header);
        center.setCenter(body);
        center.setBottom(footer);

        return center;
    }

    private HBox createCartRow(String product,int quanitity, double productPrice, int index, Stage primaryStage) {
        HBox row = new HBox();
        row.setSpacing(20);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #d3e4f0; -fx-border-color: #003049; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label productName = new Label(product);
        productName.setFont(smallerFont);
        productName.setStyle("-fx-text-fill: #003049;");

        Label productQuantity = new Label(" x"+quanitity);
        productName.setFont(smallerFont);
        productName.setStyle("-fx-text-fill: #003049;");

        Label priceLabel = new Label("$" + productPrice*quanitity+" ("+productPrice+" each)");
        priceLabel.setFont(smallerFont);
        priceLabel.setStyle("-fx-text-fill: #003049;");

        Label removeLabel = new Label("Remove");
        removeLabel.setFont(smallerFont);
        removeLabel.setStyle("-fx-text-fill: red; -fx-underline: true;");
        removeLabel.setOnMouseClicked(e -> {
            try {
                client.removeItemFromCart(product);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            Scene updatedCartMenu = null;
            try {
                updatedCartMenu = createCart(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            primaryStage.setScene(updatedCartMenu);
        });
        removeLabel.setOnMouseExited(e -> {
            removeLabel.setCursor(Cursor.DEFAULT);
        });

        removeLabel.setOnMouseEntered(e -> {
            removeLabel.setCursor(Cursor.HAND);
        });

        row.getChildren().addAll(productName,productQuantity, priceLabel, removeLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ************************** PROFILE MENU **************************

    private Scene createProfile(Stage primaryStage, double width, double height) {
        BorderPane menu = new BorderPane();
        VBox bottom = createBottom();
        VBox left = createSide(primaryStage);
        BorderPane center = createProfileCenter(primaryStage);
        GridPane top = createMainTop(primaryStage);

        Scene profileMenu = new Scene(menu, width, height);

        menu.setTop(top);
        menu.setBottom(bottom);
        menu.setLeft(left);
        menu.setCenter(center);

        primaryStage.setScene(profileMenu);
        return profileMenu;
    }

    private BorderPane createProfileCenter(Stage primaryStage) {
        BorderPane center = new BorderPane();
        HBox header = new HBox();
        GridPane body = createBody(primaryStage);

        center.setTop(header);
        center.setCenter(body);

        return center;
    }

    private GridPane createBody(Stage primaryStage) {
        GridPane body = new GridPane();
        body.setStyle("-fx-border-color: #669bbc; -fx-background-color: #f0f8ff;");
        body.setPadding(new Insets(20));
        body.setHgap(20);
        body.setVgap(10);
        body.setAlignment(Pos.CENTER);

        Label userName = new Label("Username:");
        userName.setFont(smallerFont);
        userName.setStyle("-fx-text-fill: #003049; -fx-font-weight: bold;");

        Label userGender = new Label("Gender:");
        userGender.setFont(smallerFont);
        userGender.setStyle("-fx-text-fill: #003049; -fx-font-weight: bold;");

        Label userDateOfBirth = new Label("Date of Birth:");
        userDateOfBirth.setFont(smallerFont);
        userDateOfBirth.setStyle("-fx-text-fill: #003049; -fx-font-weight: bold;");

        Label userAddress = new Label("Address:");
        userAddress.setFont(smallerFont);
        userAddress.setStyle("-fx-text-fill: #003049; -fx-font-weight: bold;");

        Label userBalance = new Label("Balance:");
        userBalance.setFont(smallerFont);
        userBalance.setStyle("-fx-text-fill: #003049; -fx-font-weight: bold;");

        Label name = new Label(client.getCurrentUser().getUsername());
        name.setFont(smallerFont);
        name.setStyle("-fx-text-fill: #003049;");

        Label gender;
        if (client.getCurrentUser() instanceof Customer) {
            gender = new Label(((Customer) client.getCurrentUser()).getGender().toString());
            gender.setFont(smallerFont);
            gender.setStyle("-fx-text-fill: #003049;");
        } else {
            gender = new Label("NA");
            gender.setFont(smallerFont);
            gender.setStyle("-fx-text-fill: #003049;");
        }

        Label dateOfBirth = new Label(client.getCurrentUser().getBirthDate().toString());
        dateOfBirth.setFont(smallerFont);
        dateOfBirth.setStyle("-fx-text-fill: #003049;");

        Label address;
        if (client.getCurrentUser() instanceof Customer) {
            address = new Label(((Customer) client.getCurrentUser()).getAddress());
            address.setFont(smallerFont);
            address.setStyle("-fx-text-fill: #003049;");
        } else {
            address = new Label("NA");
            address.setFont(smallerFont);
            address.setStyle("-fx-text-fill: #003049;");
        }

        Label balance;
        if (client.getCurrentUser() instanceof Customer) {
            balance = new Label(((Customer) client.getCurrentUser()).getBalance() + "$");
            balance.setFont(smallerFont);
            balance.setStyle("-fx-text-fill: #003049;");
        } else {
            balance = new Label("NA");
            balance.setFont(smallerFont);
            balance.setStyle("-fx-text-fill: #003049;");
        }

        HBox row1 = createRow(userName, name, "#f0f8ff");
        HBox row2 = createRow(userGender, gender, "#dbe9f4");
        HBox row3 = createRow(userDateOfBirth, dateOfBirth, "#f0f8ff");
        HBox row4 = createRow(userAddress, address, "#dbe9f4");
        HBox row5 = createRow(userBalance, balance, "#f0f8ff");


        Button editButton = new Button("Edit A User's Information");
        editButton.setOnAction(e -> openEditUserSection(primaryStage));

        body.add(row1, 0, 0);
        body.add(row2, 0, 1);
        body.add(row3, 0, 2);
        body.add(row4, 0, 3);
        body.add(row5, 0, 4);
        if (client.getCurrentUser() instanceof Admin) {
            body.add(editButton, 0, 5);
        }
        return body;
    }

    private void openEditUserSection(Stage primaryStage) {
        Stage editStage = new Stage();
        editStage.setTitle("Manipulate User Information");

        GridPane editGrid = new GridPane();
        editGrid.setPadding(new Insets(20));
        editGrid.setHgap(10);
        editGrid.setVgap(10);

        TextField oldUsernameField = new TextField();
        TextField newUsernameField = new TextField();
        TextField passwordField = new TextField();
        TextField addressField = new TextField();
        TextField balanceField = new TextField();
        DatePicker birthDatePicker = new DatePicker();
        ComboBox<Gender> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll(Gender.values());

        Button saveButton = new Button("Request Changes");
        saveButton.setOnAction(e -> {
            try {
                String oldUsername = oldUsernameField.getText();
                String newUsername = newUsernameField.getText();
                String password = passwordField.getText();
                String address = addressField.getText();
                double balance = Double.parseDouble(balanceField.getText());
                LocalDate birthDate = birthDatePicker.getValue();
                Gender gender = genderComboBox.getValue();

                if (client.updateCustomer(oldUsername, newUsername, password, birthDate, address, gender, balance).getStatus().equals("CUSTOMER_UPDATED_SUCCESSFULLY")) {
                    new Alert(Alert.AlertType.INFORMATION, "User updated successfully!").show();
                    editStage.close();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update user. Please check the input.").show();
                }
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid balance input. Please enter a valid number.").show();
            } catch (NullPointerException ex) {
                new Alert(Alert.AlertType.ERROR, "All fields must be filled out.").show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Adding fields and labels to the grid
        editGrid.add(new Label("Old Username:"), 0, 0);
        editGrid.add(oldUsernameField, 1, 0);

        editGrid.add(new Label("New Username:"), 0, 1);
        editGrid.add(newUsernameField, 1, 1);

        editGrid.add(new Label("Password:"), 0, 2);
        editGrid.add(passwordField, 1, 2);

        editGrid.add(new Label("Address:"), 0, 3);
        editGrid.add(addressField, 1, 3);

        editGrid.add(new Label("Balance:"), 0, 4);
        editGrid.add(balanceField, 1, 4);

        editGrid.add(new Label("Date of Birth:"), 0, 5);
        editGrid.add(birthDatePicker, 1, 5);

        editGrid.add(new Label("Gender:"), 0, 6);
        editGrid.add(genderComboBox, 1, 6);

        editGrid.add(saveButton, 0, 7, 2, 1); // Span across two columns

        Scene editScene = new Scene(editGrid, 400, 400);
        editStage.setScene(editScene);
        editStage.initOwner(primaryStage);
        editStage.show();
    }


    private HBox createRow(Label label, Label value, String backgroundColor) {
        HBox row = new HBox();
        row.setStyle("-fx-background-color: " + backgroundColor + "; -fx-padding: 10;");
        row.setSpacing(20);
        row.getChildren().addAll(label, value);
        return row;
    }

    // ************************** PURCHASE MENU **************************

    private Scene createPurchase(Stage primaryStage, double width, double height) {
        BorderPane menu = new BorderPane();
        VBox bottom = createBottom();
        VBox center = createPurchaseCenter(primaryStage);

        Scene purchaseMenu = new Scene(menu, width, height);

        double initialMargin = purchaseMenu.getWidth() * 0.2;
        BorderPane.setMargin(center, new Insets(0, initialMargin * 1.5, 0, initialMargin * 1.5));

        purchaseMenu.widthProperty().addListener((observable, oldValue, newValue) -> {
            double margin = newValue.doubleValue() * 0.2;
            BorderPane.setMargin(center, new Insets(0, margin * 1.5, 0, margin * 1.5));
        });

        menu.setBottom(bottom);
        menu.setCenter(center);

        primaryStage.setScene(purchaseMenu);
        return purchaseMenu;
    }

    private VBox createPurchaseCenter(Stage primaryStage) {
        VBox center = new VBox();
        center.setStyle("-fx-border-color: #669bbc; -fx-background-color: #f0f8ff");

        card.setFitHeight(180);
        card.setFitWidth(290);
        center.setAlignment(Pos.CENTER);

        Label back = new Label("Back to Cart");
        back.setUnderline(true);
        back.setFont(smallerFont);

        Label name = new Label("CardHolder Name:");
        name.setFont(smallerFont);
        TextField tfName = new TextField();
        tfName.setMinWidth(300);
        tfName.setFont(smallerFont2);
        tfName.setMaxWidth(300);
        GridPane nameBox = new GridPane();
        nameBox.add(name, 0, 0);
        nameBox.add(tfName, 0, 1);
        nameBox.setAlignment(Pos.CENTER);

        Label number = new Label("Card Number:");
        number.setFont(smallerFont);
        TextField tfNumber = new TextField();
        tfNumber.setMinWidth(300);
        tfNumber.setMaxWidth(300);
        tfNumber.setFont(smallerFont2);
        GridPane numBox = new GridPane();
        numBox.add(number, 0, 0);
        numBox.add(tfNumber, 0, 1);
        numBox.setAlignment(Pos.CENTER);

        Label expiration = new Label("Expires:");
        expiration.setFont(smallerFont);
        DatePicker tfExpiration = new DatePicker();
        tfExpiration.setMaxWidth(100);
        GridPane expBox = new GridPane();
        expBox.add(expiration, 0, 0);
        expBox.add(tfExpiration, 0, 1);
        expBox.setAlignment(Pos.CENTER);

        Label cvv = new Label("CVV:");
        cvv.setFont(smallerFont);
        TextField tfcvv = new TextField();
        tfcvv.setMaxWidth(100);
        tfcvv.setFont(smallerFont2);
        GridPane cvvBox = new GridPane();
        cvvBox.add(cvv, 0, 0);
        cvvBox.add(tfcvv, 0, 1);
        cvvBox.setAlignment(Pos.CENTER);

        Button btProceed = new Button("Proceed with Payment");
        btProceed.setFont(smallerFont);
        btProceed.setStyle("-fx-background-color: #246891;");
        btProceed.setAlignment(Pos.CENTER);

        btProceed.setOnMouseExited(e -> {
            btProceed.setCursor(Cursor.DEFAULT);
        });

        btProceed.setOnMouseEntered(e -> {
            btProceed.setCursor(Cursor.HAND);
        });

        back.setOnMouseExited(e -> {
            back.setUnderline(false);
            back.setCursor(Cursor.DEFAULT);
        });

        back.setOnMouseEntered(e -> {
            back.setUnderline(true);
            btProceed.setCursor(Cursor.HAND);
        });

        back.setOnMouseClicked(e -> {
            try {
                Scene cartMenu = createCart(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        HBox ec = new HBox();
        ec.getChildren().addAll(expBox, cvvBox);
        ec.setAlignment(Pos.CENTER);
        ec.setSpacing(100);

        VBox cardB = new VBox();
        cardB.getChildren().addAll(nameBox, numBox, ec);
        cardB.setSpacing(10);

        Label error = new Label("");
        error.setStyle("-fx-text-fill: red");
        error.setFont(smallerFont2);
        error.setVisible(false);

        btProceed.setOnMouseClicked(e -> {
            if (tfcvv.getText().isEmpty() || tfName.getText().isEmpty() || tfNumber.getText().isEmpty() || tfExpiration.getValue() == null) {
                error.setText("Fill all fields before proceeding!");
                error.setVisible(true);
            } else if (tfNumber.getText().length() != 12) {
                error.setText("Card Number must be 12 characters!");
                error.setVisible(true);
            } else if (tfcvv.getText().length() != 3) {
                error.setText("CVV must be 3 characters!");
                error.setVisible(true);
            } else if (!tfcvv.getText().matches("\\d+")) {
                error.setText("CVV must be numbers!");
                error.setVisible(true);
            }
            else {
                try {
                    Scene MainMenu = createMain(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                userProducts.clear();
                prices.clear();
            }
        });

        VBox all = new VBox();
        VBox half = new VBox();
        half.getChildren().addAll(card, cardB);
        half.setAlignment(Pos.CENTER);
        half.setSpacing(30);
        all.getChildren().addAll(half, error, btProceed);
        all.setSpacing(10);
        all.setAlignment(Pos.CENTER);

        center.getChildren().addAll(all, back);
        center.setSpacing(10);

        return center;
    }

    //  ************************** MAIN MENU **************************

    private Scene createMain(Stage primaryStage, double width, double height) throws IOException {
        BorderPane menu = new BorderPane();
        ScrollPane center = createMainCenter(primaryStage);
        GridPane top = createMainTop(primaryStage);
        VBox side = createSide(primaryStage);

        menu.setTop(top);
        menu.setLeft(side);
        menu.setCenter(center);

        Scene mainMenu = new Scene(menu, width, height);

        primaryStage.setScene(mainMenu);
        return mainMenu;
    }

    private GridPane createMainTop(Stage primaryStage) {
        GridPane top = new GridPane();
        top.setStyle("-fx-background-color: #003049");
        top.setMinHeight(80);

        icon2.setPreserveRatio(true);
        icon2.setFitHeight(50);

        pfp2.setPreserveRatio(true);
        pfp2.setFitHeight(50);

        Label title = new Label("The Corner Shop");
        title.setFont(titleFont);
        title.setStyle("-fx-text-fill: white");
        title.setTranslateY(10);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setTextAlignment(TextAlignment.CENTER);

        HBox titcon = new HBox();
        titcon.getChildren().addAll(icon2, title);
        titcon.setSpacing(10);

        Label customerUsername = new Label(client.getCurrentUser().getUsername());
        customerUsername.setFont(smallerFont);
        customerUsername.setStyle("-fx-text-fill: white");

        HBox profileInfo = new HBox();
        profileInfo.setSpacing(10);
        profileInfo.setAlignment(Pos.CENTER);
        profileInfo.getChildren().addAll(pfp2, customerUsername);


        Label cart = new Label("Cart");
        cart.setFont(smallerFont);
        cart.setStyle("-fx-text-fill: white");

        HBox profcart = new HBox();
        if(client.getCurrentUser() instanceof Customer) {
            profcart.getChildren().addAll(profileInfo, cart);
        }else{
            profcart.getChildren().addAll(profileInfo);
        }

        profcart.setSpacing(35);
        profcart.setAlignment(Pos.CENTER_RIGHT);

        top.setPadding(new Insets(15, 35, 15, 10));

        ColumnConstraints leftCol = new ColumnConstraints();

        ColumnConstraints midCol = new ColumnConstraints();
        midCol.setHgrow(Priority.ALWAYS);

        ColumnConstraints rightCol = new ColumnConstraints();
        rightCol.setPercentWidth(30);

        top.getColumnConstraints().addAll(leftCol, midCol, rightCol);

        icon2.setOnMouseClicked(event -> {
            RotateTransition rotate = new RotateTransition(Duration.seconds(0.5), icon2);
            rotate.setByAngle(360);
            rotate.play();

            toggleSidebar(primaryStage);
        });

        icon2.setOnMouseExited(e -> {
            icon2.setCursor(Cursor.DEFAULT);
        });

        icon2.setOnMouseEntered(e -> {
            icon2.setCursor(Cursor.HAND);
        });

        title.setOnMouseClicked(event -> {
            try {
                Scene MainMenu = createMain(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        title.setOnMouseExited(e -> {
            title.setCursor(Cursor.DEFAULT);
        });

        title.setOnMouseEntered(e -> {
            title.setCursor(Cursor.HAND);
        });

        profileInfo.setOnMouseExited(e -> {
            customerUsername.setUnderline(false);
            profileInfo.setCursor(Cursor.DEFAULT);
        });

        profileInfo.setOnMouseEntered(e -> {
            customerUsername.setUnderline(true);
            profileInfo.setCursor(Cursor.HAND);
        });

        profileInfo.setOnMouseClicked(e -> {
            Scene profileMenu = createProfile(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
        });

        cart.setOnMouseExited(e -> {
            cart.setUnderline(false);
            cart.setCursor(Cursor.DEFAULT);
        });

        cart.setOnMouseEntered(e -> {
            cart.setUnderline(true);
            cart.setCursor(Cursor.HAND);
        });

        cart.setOnMouseClicked(e -> {
            try {
                Scene cartMenu = createCart(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        top.add(titcon, 0, 0);

        top.add(profcart, 2, 0);
        top.setHgap(70);

        return top;
    }

    private VBox createSide(Stage primaryStage) {
        VBox sideNav = new VBox();
        sideNav.setStyle("-fx-background-color: #669bbc;");
        sideNav.setPadding(new Insets(10, 10, 10, 10));
        sideNav.setPrefWidth(180);
        sideNav.setTranslateX(-200);
        sideNav.prefHeightProperty().bind(primaryStage.heightProperty());

        Label home = new Label("Home");
        home.setFont(smallerFont2);
        home.setStyle("-fx-text-fill: #003049; -fx-font-weight: BOLD");

        Label profile = new Label("Profile");
        profile.setFont(smallerFont2);
        profile.setStyle("-fx-text-fill: #003049; -fx-font-weight: BOLD");

        Label cart = new Label("Cart");
        cart.setFont(smallerFont2);
        cart.setStyle("-fx-text-fill: #003049; -fx-font-weight: BOLD");

        Label logOut = new Label("Log Out");
        logOut.setFont(smallerFont2);
        logOut.setStyle("-fx-text-fill: #003049; -fx-font-weight: BOLD");

        home.setOnMouseExited(e -> {
            home.setUnderline(false);
            home.setCursor(Cursor.DEFAULT);
        });

        home.setOnMouseEntered(e -> {
            home.setUnderline(true);
            home.setCursor(Cursor.HAND);
        });

        home.setOnMouseClicked(e -> {
            try {
                Scene MainMenu = createMain(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        profile.setOnMouseExited(e -> {
            profile.setUnderline(false);
            profile.setCursor(Cursor.DEFAULT);
        });

        profile.setOnMouseEntered(e -> {
            profile.setUnderline(true);
            profile.setCursor(Cursor.HAND);
        });

        profile.setOnMouseClicked(e -> {
            Scene profileMenu = createProfile(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
        });

        cart.setOnMouseExited(e -> {
            cart.setUnderline(false);
            cart.setCursor(Cursor.DEFAULT);
        });

        cart.setOnMouseEntered(e -> {
            cart.setUnderline(true);
            cart.setCursor(Cursor.HAND);
        });

        cart.setOnMouseClicked(e -> {
            try {
                Scene cartMenu = createCart(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        logOut.setOnMouseExited(e -> {
            logOut.setUnderline(false);
            logOut.setCursor(Cursor.DEFAULT);
        });

        logOut.setOnMouseEntered(e -> {
            logOut.setUnderline(true);
            logOut.setCursor(Cursor.HAND);
        });

        logOut.setOnMouseClicked(e -> {
            Scene loginMenu = createLogin(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
            client.setCurrentUser(null);
        });

        sideNav.setPadding(new Insets(15));
        sideNav.setSpacing(15);
        if(client.getCurrentUser() instanceof Customer){
            sideNav.getChildren().addAll(home, profile, cart, logOut);
        }else{
            sideNav.getChildren().addAll(home, profile, logOut);
        }


        return sideNav;
    }

    private void toggleSidebar(Stage primaryStage) {
        VBox sideNav = (VBox) ((BorderPane) primaryStage.getScene().getRoot()).getLeft();

        TranslateTransition slideTransition = new TranslateTransition();
        slideTransition.setNode(sideNav);
        slideTransition.setDuration(Duration.seconds(0.2));

        if (sideNav.getTranslateX() == 0) {
            slideTransition.setToX(-200);
        } else {
            slideTransition.setToX(0);
        }
        slideTransition.play();
    }

    private ScrollPane createMainCenter(Stage primaryStage) throws IOException {
        ScrollPane mainScrollPane = new ScrollPane();
        VBox categoriesBox = new VBox();
        categoriesBox.setPadding(new Insets(20));
        categoriesBox.setSpacing(30);


        Label addCategoryLabel = new Label("Add Category");
        addCategoryLabel.setStyle("-fx-text-fill: #4dff00; -fx-cursor: hand;");
        addCategoryLabel.setOnMouseClicked(event -> showAddCategoryWindow());

        Label addProductLabel = new Label("Add Product");
        addProductLabel.setStyle("-fx-text-fill: #4dff00; -fx-cursor: hand;");
        addProductLabel.setOnMouseClicked(event -> showAddProductWindow());


        Label editCategoryLabel = new Label("Edit Category");
        editCategoryLabel.setStyle("-fx-text-fill: #ecd708; -fx-cursor: hand;");
        editCategoryLabel.setOnMouseClicked(event -> showEditCategoryWindow());

        Label editProductLabel = new Label("Edit Product");
        editProductLabel.setStyle("-fx-text-fill: #ecd708; -fx-cursor: hand;");
        editProductLabel.setOnMouseClicked(event -> showEditProductWindow());


        Label removeCategoryLabel = new Label("Remove Category");
        removeCategoryLabel.setStyle("-fx-text-fill: red; -fx-cursor: hand;");
        removeCategoryLabel.setOnMouseClicked(event -> showRemoveCategoryWindow());

        Label removeProductLabel = new Label("Remove Product");
        removeProductLabel.setStyle("-fx-text-fill: red; -fx-cursor: hand;");
        removeProductLabel.setOnMouseClicked(event -> showRemoveProductWindow());

        if(client.getCurrentUser() instanceof Admin){
            VBox labelBox = new VBox(10, addCategoryLabel, addProductLabel, editCategoryLabel, editProductLabel, removeCategoryLabel, removeProductLabel);
            categoriesBox.getChildren().add(labelBox);
        }



        Response response = client.getCategories();
        ArrayList<Category> currentCategories = client.getMapper().convertValue(response.getData().get("categories"), new TypeReference<ArrayList<Category>>() {});

        for (Category category : currentCategories) {
            VBox categoryBox = createCategoryBox(category.getName(), category.getProducts());
            categoriesBox.getChildren().add(categoryBox);
        }


        mainScrollPane.setContent(categoriesBox);
        mainScrollPane.setFitToWidth(true);

        return mainScrollPane;
    }

    private void showAddCategoryWindow() {
        Stage addCategoryStage = new Stage();
        VBox addCategoryLayout = new VBox(10);


        TextField categoryNameField = new TextField();
        categoryNameField.setPromptText("Enter category name");

        TextField categoryDescriptionField = new TextField();
        categoryDescriptionField.setPromptText("Enter category description");

        TextField categoryIdField = new TextField();
        categoryIdField.setPromptText("Enter category ID");


        Button addButton = new Button("Request to Add Category");
        addButton.setOnAction(e -> {
            String categoryName = categoryNameField.getText();
            String categoryDescription = categoryDescriptionField.getText();
            String categoryId = categoryIdField.getText();

            if (!categoryName.isEmpty() && !categoryDescription.isEmpty() && !categoryId.isEmpty()) {
                try {
                    client.addCategory(categoryName,categoryDescription,categoryId);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                addCategoryStage.close();
            } else {
                System.out.println("Please fill in all fields.");
            }
        });

        addCategoryLayout.getChildren().addAll(new Label("Add Category"), categoryNameField, categoryDescriptionField, categoryIdField, addButton);


        Scene addCategoryScene = new Scene(addCategoryLayout, 300, 250);
        addCategoryStage.setScene(addCategoryScene);
        addCategoryStage.show();
    }


    private void showAddProductWindow() {
        Stage addProductStage = new Stage();
        VBox addProductLayout = new VBox(10);

        TextField categoryNameField = new TextField();
        categoryNameField.setPromptText("Enter Category name");

        TextField productNameField = new TextField();
        productNameField.setPromptText("Enter product name");

        TextField productDescriptionField = new TextField();
        productDescriptionField.setPromptText("Enter product description");

        TextField productPriceField = new TextField();
        productPriceField.setPromptText("Enter product price");

        DatePicker expirationDatePicker = new DatePicker();
        expirationDatePicker.setPromptText("Select expiration date");

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String categoryName = categoryNameField.getText();
            String productName = productNameField.getText();
            String productDescription = productDescriptionField.getText();
            String productPrice = productPriceField.getText();
            LocalDate expirationDate = expirationDatePicker.getValue();

            if (!productName.isEmpty() && !productDescription.isEmpty() && !productPrice.isEmpty() && expirationDate != null) {
                try {
                    client.addProduct(categoryName,productName,productDescription,Double.parseDouble(productPrice),expirationDate);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                addProductStage.close();
            } else {
                System.out.println("Please fill in all fields.");
            }
        });

        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(addButton);

        addProductLayout.getChildren().addAll(
                new Label("Add Product"),
                categoryNameField,
                productNameField,
                productDescriptionField,
                productPriceField,
                expirationDatePicker,
                buttonContainer
        );

        Scene addProductScene = new Scene(addProductLayout, 300, 300);
        addProductStage.setScene(addProductScene);
        addProductStage.show();
    }

    private void showEditCategoryWindow() {
        Stage editCategoryStage = new Stage();
        VBox editCategoryLayout = new VBox(10);

        TextField oldCategoryNameField = new TextField();
        oldCategoryNameField.setPromptText("Enter old category name");

        TextField newCategoryNameField = new TextField();
        newCategoryNameField.setPromptText("Enter new category name");

        TextField newCategoryDescriptionField = new TextField();
        newCategoryDescriptionField.setPromptText("Enter new category description");

        TextField newCategoryIdField = new TextField();
        newCategoryIdField.setPromptText("Enter new category ID");

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            String oldCategoryName = oldCategoryNameField.getText();
            String newCategoryName = newCategoryNameField.getText();
            String newCategoryDescription = newCategoryDescriptionField.getText();
            String newCategoryId = newCategoryIdField.getText();

            if (!oldCategoryName.isEmpty() && !newCategoryName.isEmpty() && !newCategoryDescription.isEmpty() && !newCategoryId.isEmpty()) {
                try {
                    client.updateCategory(oldCategoryName,newCategoryName,newCategoryDescription,newCategoryId);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                editCategoryStage.close();
            } else {
                System.out.println("Please fill in all fields.");
            }
        });


        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(editButton);

        editCategoryLayout.getChildren().addAll(
                new Label("Edit Category"),
                oldCategoryNameField,
                newCategoryNameField,
                newCategoryDescriptionField,
                newCategoryIdField,
                buttonContainer
        );

        Scene editCategoryScene = new Scene(editCategoryLayout, 300, 350);
        editCategoryStage.setScene(editCategoryScene);
        editCategoryStage.show();
    }



    private void showEditProductWindow() {
        Stage editProductStage = new Stage();
        VBox editProductLayout = new VBox(10);

        TextField oldProductNameField = new TextField();
        oldProductNameField.setPromptText("Enter old product name");

        TextField newProductNameField = new TextField();
        newProductNameField.setPromptText("Enter new product name");

        TextField newProductDescriptionField = new TextField();
        newProductDescriptionField.setPromptText("Enter new product description");

        TextField newProductPriceField = new TextField();
        newProductPriceField.setPromptText("Enter new product price");

        DatePicker newExpirationDatePicker = new DatePicker();
        newExpirationDatePicker.setPromptText("Select new expiration date");

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            String oldProductName = oldProductNameField.getText();
            String newProductName = newProductNameField.getText();
            String newProductDescription = newProductDescriptionField.getText();
            String newProductPrice = newProductPriceField.getText();
            LocalDate newExpirationDate = newExpirationDatePicker.getValue();

            if (!oldProductName.isEmpty() && !newProductName.isEmpty() && !newProductDescription.isEmpty() && !newProductPrice.isEmpty() && newExpirationDate != null) {
                try {
                    client.updateProduct(oldProductName,newProductName,newProductDescription,Double.parseDouble(newProductPrice),newExpirationDate);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                editProductStage.close();
            } else {
                System.out.println("Please fill in all fields.");
            }
        });

        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(editButton);


        editProductLayout.getChildren().addAll(
                new Label("Edit Product"),
                oldProductNameField,
                newProductNameField,
                newProductDescriptionField,
                newProductPriceField,
                newExpirationDatePicker,
                buttonContainer
        );


        Scene editProductScene = new Scene(editProductLayout, 300, 350);
        editProductStage.setScene(editProductScene);
        editProductStage.show();
    }


    private void showRemoveCategoryWindow() {
        Stage removeCategoryStage = new Stage();
        VBox removeCategoryLayout = new VBox(10);

        TextField categoryNameField = new TextField();
        categoryNameField.setPromptText("Enter category name to remove");

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> {
            String categoryName = categoryNameField.getText();
            if (!categoryName.isEmpty()) {
                try {
                    client.deleteCategory(categoryName);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                removeCategoryStage.close();
            }
        });

        removeCategoryLayout.getChildren().addAll(new Label("Remove Category"), categoryNameField, removeButton);
        Scene removeCategoryScene = new Scene(removeCategoryLayout, 300, 200);
        removeCategoryStage.setScene(removeCategoryScene);
        removeCategoryStage.show();
    }

    private void showRemoveProductWindow() {
        Stage removeProductStage = new Stage();
        VBox removeProductLayout = new VBox(10);

        TextField productNameField = new TextField();
        productNameField.setPromptText("Enter product name to remove");

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> {
            String productName = productNameField.getText();
            if (!productName.isEmpty()) {
                try {
                    client.deleteProduct(productName);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                removeProductStage.close();
            }
        });

        removeProductLayout.getChildren().addAll(new Label("Remove Product"), productNameField, removeButton);
        Scene removeProductScene = new Scene(removeProductLayout, 300, 200);
        removeProductStage.setScene(removeProductScene);
        removeProductStage.show();
    }



    private VBox createCategoryBox(String categoryName, ArrayList<Product> products) {
        VBox categoryBox = new VBox();
        categoryBox.setSpacing(10);

        Label categoryLabel = new Label(categoryName);
        categoryLabel.setFont(categoryFont);
        categoryLabel.setStyle("-fx-text-fill: #003049;");

        HBox productPane = new HBox();
        HBox productContainer = new HBox();
        productContainer.setSpacing(20);
        productContainer.setPadding(new Insets(10));

        for (Product product : products) {
            VBox productBox = createProductBox(product.getName(), product.getDescription(), String.valueOf(product.getPrice()));
            productContainer.getChildren().add(productBox);
        }

        productPane.getChildren().add(productContainer);
        productPane.setStyle("-fx-border-color: #003049; -fx-border-radius: 10; -fx-background-radius: 10;");

        categoryBox.getChildren().addAll(categoryLabel, productPane);

        return categoryBox;
    }



    private VBox createProductBox(String name,String description, String price ) {
        VBox productBox = new VBox();
        productBox.setSpacing(10);
        productBox.setPadding(new Insets(10));
        productBox.setStyle("-fx-background-color: #d3e4f0; -fx-border-color: #003049; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label productName = new Label(name);
        productName.setFont(productNameFont);
        productName.setStyle("-fx-text-fill: #003049;");

        Label productPrice = new Label("$ "+price);
        productPrice.setFont(productPriceFont);
        productPrice.setStyle("-fx-text-fill: #003049;");

        Label productDescription = new Label(description);
        productDescription.setFont(productDescFont);
        productDescription.setWrapText(true);
        productDescription.setStyle("-fx-text-fill: #555555;");

        Button purchaseButton = new Button("Add to Cart");
        purchaseButton.setStyle("-fx-background-color: #003049; -fx-text-fill: white; -fx-font-weight: bold;");

        purchaseButton.setOnAction(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), purchaseButton);
            scaleTransition.setByX(0.1);
            scaleTransition.setByY(0.1);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(2);
            scaleTransition.play();

            try {
                client.addToCart(productName.getText(),1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        purchaseButton.setOnMouseExited(e -> {
            purchaseButton.setCursor(Cursor.DEFAULT);
        });

        purchaseButton.setOnMouseEntered(e -> {
            purchaseButton.setCursor(Cursor.HAND);
        });
        if(client.getCurrentUser() instanceof Customer){
            productBox.getChildren().addAll(productName, productPrice, productDescription, purchaseButton);
        }else{
            productBox.getChildren().addAll(productName, productPrice, productDescription);
        }


        return productBox;
    }

    // ************************** REGISTER MENU **************************

    private Scene createRegister(Stage primaryStage, double width, double height) {
        BorderPane menu = new BorderPane();
        VBox bottom = createBottom();
        ScrollPane centerScroll = createScrollableRegisterCenter(primaryStage);

        Scene registerMenu = new Scene(menu, width, height);

        double initialMargin = registerMenu.getWidth() * 0.2;
        BorderPane.setMargin(centerScroll, new Insets(0, initialMargin, 0, initialMargin));

        registerMenu.widthProperty().addListener((observable, oldValue, newValue) -> {
            double margin = newValue.doubleValue() * 0.2;
            BorderPane.setMargin(centerScroll, new Insets(0, margin, 0, margin));
        });

        menu.setBottom(bottom);
        menu.setCenter(centerScroll);

        primaryStage.setScene(registerMenu);
        return registerMenu;
    }

    private ScrollPane createScrollableRegisterCenter(Stage primaryStage) {
        BorderPane center = createRegisterCenter(primaryStage);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(center);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        return scrollPane;
    }


    private BorderPane createRegisterCenter(Stage primaryStage) {
        ArrayList<String> governorates = new ArrayList<>();
        governorates.add("Cairo"); governorates.add("Alexandria"); governorates.add("Giza"); governorates.add("Luxor"); governorates.add("Aswan"); governorates.add("Port Said"); governorates.add("Suez"); governorates.add("Sharm El Sheikh"); governorates.add("Mansoura"); governorates.add("Tanta"); governorates.add("Damanhour"); governorates.add("Ismailia"); governorates.add("Minya"); governorates.add("Beni Suef"); governorates.add("Fayoum"); governorates.add("Asyut"); governorates.add("Qena"); governorates.add("Sohag"); governorates.add("Kafr El Sheikh"); governorates.add("Dakahlia"); governorates.add("Gharbia"); governorates.add("Matruh"); governorates.add("North Sinai"); governorates.add("South Sinai"); governorates.add("Red Sea"); governorates.add("Beheira"); governorates.add("New Valley"); governorates.add("Damietta"); governorates.add("Qalyubia"); governorates.add("Monufia"); governorates.add("Minya");

        BorderPane center = new BorderPane();
        center.setStyle("-fx-border-color: #669bbc; -fx-background-color: #f0f8ff");
        VBox box = new VBox();
        HBox date = new HBox();
        VBox yarabA5ls = new VBox();
        VBox address = new VBox();

        Label regUsername = new Label("Username:");
        regUsername.setFont(smallerFont);
        TextField tfRegUsername = new TextField();
        tfRegUsername.setFont(smallerFont2);

        Label regPassword = new Label("Password:");
        regPassword.setFont(smallerFont);
        PasswordField tfRegPassword = new PasswordField();
        tfRegPassword.setFont(smallerFont2);

        Label regConfirmPassword = new Label("Confirm Password:");
        regConfirmPassword.setFont(smallerFont);
        PasswordField tfRegConfirmPassword = new PasswordField();
        tfRegConfirmPassword.setFont(smallerFont2);

        Label regDate = new Label("Date of Birth:");
        regDate.setFont(smallerFont);

        Label regDay = new Label("D:");
        regDay.setFont(smallerFont);
        ComboBox tfDay = new ComboBox();
        for (int day = 1; day <= 31; day++) {
            tfDay.getItems().add(day);
        }
        tfDay.setMinWidth(25);
        tfDay.setVisibleRowCount(8);

        Label regMonth = new Label("M:");
        regMonth.setFont(smallerFont);
        ComboBox tfMonth = new ComboBox();
        for (int day = 1; day <= 12; day++) {
            tfMonth.getItems().add(day);
        }
        tfMonth.setMinWidth(25);
        tfMonth.setVisibleRowCount(8);

        Label regYear = new Label("Y:");
        regYear.setFont(smallerFont);
        TextField tfYear = new TextField();
        tfYear.setPrefColumnCount(3);
        tfYear.setMaxWidth(95);
        tfYear.setMinWidth(95);
        tfYear.setFont(smallerFont2);

        Label regGovern = new Label("Governorate:");
        regGovern.setFont(smallerFont);
        ComboBox tfGovern = new ComboBox();
        GridPane governBox = new GridPane();
        governBox.add(regGovern, 0, 0);
        governBox.add(tfGovern, 0, 1);
        tfGovern.getItems().addAll(governorates);

        Label regCity = new Label("City:");
        regCity.setFont(smallerFont);
        TextField tfCity = new TextField();
        GridPane cityBox = new GridPane();
        cityBox.add(regCity, 0, 0);
        cityBox.add(tfCity, 0, 1);
        tfCity.setFont(smallerFont2);
        tfCity.setMaxWidth(150);

        HBox gc = new HBox();
        gc.setSpacing(50);
        gc.getChildren().addAll(governBox, cityBox);

        Label regAddress = new Label("Address:");
        regAddress.setFont(smallerFont);
        TextField tfAddress = new TextField();
        tfAddress.setMinWidth(332);
        tfAddress.setFont(smallerFont2);
        GridPane addressBox = new GridPane();
        addressBox.add(regAddress, 0, 0);
        addressBox.add(tfAddress, 0, 1);

        Label regGender = new Label("Gender:");
        regGender.setFont(smallerFont);
        ComboBox<String> tfGender = new ComboBox<>();
        tfGender.getItems().addAll("Male", "Female");

        Label error = new Label("");
        error.setStyle("-fx-text-fill: red");
        error.setFont(smallerFont2);
        error.setVisible(false);

        GridPane gender = new GridPane();
        gender.add(regGender, 0, 0);
        gender.add(tfGender, 1, 0);
        gender.setHgap(15);

        box.setSpacing(5);
        address.setSpacing(15);
        yarabA5ls.setSpacing(5);
        date.setSpacing(20);
        box.getChildren().addAll(regUsername, tfRegUsername, regPassword, tfRegPassword, regConfirmPassword, tfRegConfirmPassword);
        date.getChildren().addAll(regDay, tfDay, regMonth, tfMonth, regYear, tfYear);
        address.getChildren().addAll(gc, addressBox);

        Label welcome = new Label("Welcome to The Corner Shop.");
        welcome.setFont(biggerFont);
        welcome.setStyle("-fx-text-fill: #f0f8ff");

        Label welcome2 = new Label("Please register to continue.");
        welcome2.setFont(biggerFont);
        welcome2.setStyle("-fx-text-fill: #f0f8ff");

        Button btRegister = new Button("Register");
        btRegister.setMinSize(340, 20);
        btRegister.setFont(generalFont);
        btRegister.setStyle("-fx-background-color: #246891; -fx-text-fill: #f0f8ff");

        Label login = new Label("Already registered? Click here to login");
        login.setPadding(new Insets(20));
        login.setFont(smallerFont);

        btRegister.setOnMouseClicked(e -> {
            if (tfRegUsername.getText().isEmpty() ||
                    tfRegPassword.getText().isEmpty() ||
                    tfRegConfirmPassword.getText().isEmpty() ||
                    tfYear.getText().isEmpty() ||
                    tfGovern.getSelectionModel().isEmpty() ||
                    tfCity.getText().isEmpty() ||
                    tfAddress.getText().isEmpty() ||
                    tfGender.getSelectionModel().isEmpty() ||
                    tfDay.getSelectionModel().isEmpty() ||
                    tfMonth.getSelectionModel().isEmpty()) {
                error.setText("Fill all fields to register!");
                error.setVisible(true);
            } else if (tfRegPassword.getText().length() < 8) {
                error.setText("Password must be atleast 8 characters!");
                error.setVisible(true);
            } else if (!tfRegPassword.getText().equals(tfRegConfirmPassword.getText())) {
                error.setText("Password and Confirm Password don't match!");
                error.setVisible(true);
            } else if (!tfYear.getText().matches("\\d+") || tfYear.getText().length() != 4 || Integer.valueOf(tfYear.getText()) < 1900 || Integer.valueOf(tfYear.getText()) > 2015) {
                error.setText("Year must be inputted correctly!");
                error.setVisible(true);
            }
            else {
                int day = Integer.parseInt(tfDay.getSelectionModel().getSelectedItem().toString());
                int month = Integer.parseInt(tfMonth.getSelectionModel().getSelectedItem().toString());
                int year = Integer.parseInt(tfYear.getText());
                String myaddress = tfGovern.getSelectionModel().getSelectedItem() + ", " + tfCity.getText() + ", " + tfAddress.getText();
                try {
                    client.signUp(tfRegUsername.getText(),tfRegPassword.getText(), LocalDate.of(year,month,day),myaddress, Gender.valueOf(tfGender.getSelectionModel().getSelectedItem().toUpperCase()),0);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Scene loginMenu = createLogin(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
            }
        });

        btRegister.setOnMouseEntered(e -> {
            btRegister.setCursor(Cursor.HAND);
        });

        btRegister.setOnMouseExited(e -> {
            btRegister.setCursor(Cursor.DEFAULT);
        });

        login.setOnMouseExited(e -> {
            login.setUnderline(false);
            login.setCursor(Cursor.DEFAULT);
        });

        login.setOnMouseEntered(e -> {
            login.setUnderline(true);
            login.setCursor(Cursor.HAND);
        });

        login.setOnMouseClicked(e -> {
            Scene loginMenu = createLogin(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
        });

        VBox centerTop = new VBox();
        VBox centerCenter = new VBox();
        VBox centerBottom = new VBox();
        GridPane coco = new GridPane();

        coco.getChildren().addAll();

        coco.add(box, 0, 0);
        coco.add(date, 0, 1);
        coco.add(address, 0, 2);
        coco.add(gender, 0, 3);
        GridPane.setColumnSpan(btRegister, 3);
        GridPane.setColumnSpan(tfAddress, 3);

        coco.setPadding(new Insets(15));
        coco.setVgap(25);
        coco.setHgap(10);
        coco.setAlignment(Pos.CENTER);

        centerCenter.getChildren().addAll(coco, error, btRegister);
        centerCenter.setSpacing(10);
        centerCenter.setAlignment(Pos.CENTER);

        date.setSpacing(10);

        centerTop.setStyle("-fx-background-color: #669bbc");
        centerTop.getChildren().add(welcome);
        centerTop.getChildren().add(welcome2);
        centerTop.setAlignment(Pos.CENTER);
        centerTop.setMinHeight(80);

        centerBottom.getChildren().addAll(login);
        centerBottom.setMinHeight(10);
        centerBottom.setAlignment(Pos.CENTER);

        center.setCenter(centerCenter);
        center.setTop(centerTop);
        center.setBottom(centerBottom);

        return center;
    }


    //  ************************** LOG IN MENU **************************

    private Scene createLogin(Stage primaryStage, double width, double height) {
        BorderPane menu = new BorderPane();
        VBox bottom = createBottom();
        BorderPane center = createCenter(primaryStage);

        Scene loginMenu = new Scene(menu, width, height);

        double initialMargin = loginMenu.getWidth() * 0.2;
        BorderPane.setMargin(center, new Insets(initialMargin / 3, initialMargin, initialMargin / 5, initialMargin));

        loginMenu.widthProperty().addListener((observable, oldValue, newValue) -> {
            double margin = newValue.doubleValue() * 0.2;
            BorderPane.setMargin(center, new Insets(margin / 3, margin, margin / 5, margin));
        });

        menu.setBottom(bottom);
        menu.setCenter(center);

        primaryStage.setScene(loginMenu);
        return loginMenu;
    }

    private VBox createBottom() {
        VBox bottom = new VBox();
        bottom.setMinHeight(70);
        Label bottomLabel = new Label("© 2024 The Corner Shop. All Rights Reserved.\n");
        bottomLabel.setFont(smallestFont);
        bottomLabel.setStyle("-fx-text-fill: white");
        bottomLabel.setAlignment(Pos.CENTER);
        bottom.setStyle("-fx-background-color: #003049;");
        bottom.setPadding(new Insets(20, 20, 20, 20));
        bottom.setSpacing(10);
        bottom.getChildren().addAll(bottomLabel);
        return bottom;
    }

    private BorderPane createCenter(Stage primaryStage) {
        BorderPane center = new BorderPane();
        center.setStyle("-fx-border-color: #669bbc; -fx-background-color: #f0f8ff; -fx-border-radius: 5");

        Label logUsername = new Label("Username:");
        logUsername.setFont(smallerFont);
        TextField tfLogUsername = new TextField();
        tfLogUsername.setMinWidth(190);
        tfLogUsername.setFont(smallerFont2);

        Label logPassword = new Label("Password:");
        logPassword.setFont(smallerFont);
        PasswordField tfLogPassword = new PasswordField();
        tfLogPassword.setFont(smallerFont2);

        Label welcome = new Label("Welcome to The Corner Shop.");
        welcome.setFont(biggerFont);
        welcome.setStyle("-fx-text-fill: #f0f8ff");

        Label welcome2 = new Label("Please login to continue.");
        welcome2.setFont(biggerFont);
        welcome2.setStyle("-fx-text-fill: #f0f8ff");

        Button btContinue = new Button("Log In");
        btContinue.setMinSize(300, 20);
        btContinue.setFont(smallerFont);
        btContinue.setStyle("-fx-background-color: #246891; -fx-text-fill: #f0f8ff");

        Label register = new Label("Not logged in? Click here to register");
        register.setFont(smallerFont);

        Label error = new Label("");
        error.setStyle("-fx-text-fill: red");
        error.setFont(smallerFont);
        error.setVisible(false);

        Label back = new Label("Back");
        back.setUnderline(true);
        back.setFont(smallerFont);

        btContinue.setOnMouseClicked(e -> {
            if (tfLogUsername.getText().isEmpty() || tfLogPassword.getText().isEmpty()) {
                error.setText("Fill all text fields!");
                error.setVisible(true);
                return;
            }
            String success;
            Response loginResponse;
            try {
                loginResponse = client.login(tfLogUsername.getText(), tfLogPassword.getText());
                success = loginResponse.getStatus();
                System.out.println(success);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (!Objects.equals(success, "LOGIN_SUCCESSFUL")) {
                error.setText("Invalid Login, Please Try Again!");
                error.setVisible(true);
            } else {
                currentUser = client.getCurrentUser();
                try {
                    Scene MainMenu = createMain(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        btContinue.setOnMouseEntered(e -> {
            btContinue.setCursor(Cursor.HAND);
        });

        btContinue.setOnMouseExited(e -> {
            btContinue.setCursor(Cursor.DEFAULT);
        });

        register.setOnMouseExited(e -> {
            register.setUnderline(false);
            register.setCursor(Cursor.DEFAULT);
        });

        register.setOnMouseEntered(e -> {
            register.setUnderline(true);
            register.setCursor(Cursor.HAND);
        });

        register.setOnMouseClicked(e -> {
            Scene registerMenu = createRegister(primaryStage, primaryStage.getWidth() - widthDif, primaryStage.getHeight() - heightDif);
        });

        VBox centerTop = new VBox();
        VBox centerCenter = new VBox();
        GridPane inpBox = new GridPane();

        inpBox.setVgap(15);
        inpBox.setHgap(10);
        inpBox.setAlignment(Pos.CENTER);

        inpBox.add(logUsername, 0, 0);
        inpBox.add(tfLogUsername, 1, 0);
        inpBox.add(logPassword, 0, 1);
        inpBox.add(tfLogPassword, 1, 1);
        inpBox.add(btContinue, 0, 3);

        centerCenter.setPadding(new Insets(20, 0, 0, 5));
        centerCenter.setSpacing(15);
        centerCenter.setAlignment(Pos.CENTER);

        centerCenter.getChildren().addAll(inpBox, error, register);

        GridPane.setColumnSpan(btContinue, 3);

        centerTop.setStyle("-fx-background-color: #669bbc");
        centerTop.getChildren().add(welcome);
        centerTop.getChildren().add(welcome2);
        centerTop.setAlignment(Pos.CENTER);
        centerTop.setMinHeight(80);

        center.setCenter(centerCenter);
        center.setTop(centerTop);

        return center;
    }

    public static void main(String[] args) {
        launch(args);
    }}