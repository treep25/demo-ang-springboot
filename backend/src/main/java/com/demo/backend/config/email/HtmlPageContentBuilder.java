package com.demo.backend.config.email;

import com.demo.backend.user.model.User;
import org.joda.time.DateTime;

public class HtmlPageContentBuilder {

    public static String generateHtmlContent(User user) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                                
                <head>
                    <meta charset="UTF-8"></meta>
                    <meta http-equiv="X-UA-Compatible" content="IE=edge"></meta>
                    <title>ANGULAR x SPRING BOOT</title>
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background-color: #f4f4f4;
                            color: #333;
                            margin: 20px;
                            line-height: 1.6;
                        }
                                
                        h1 {
                            color: #3498db;
                            text-align: center;
                            font-size: 28px;
                        }
                                
                        .user-info {
                            margin-top: 20px;
                            padding: 20px;
                            border: 1px solid #ddd;
                            background-color: #fff;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                            border-radius: 8px;
                        }
                                
                        .label {
                            font-weight: bold;
                            color: #555;
                            font-size: 16px;
                        }
                                
                        .value {
                            margin-left: 10px;
                            font-size: 16px;
                            color: #333;
                        }
                                
                        footer {
                            margin-top: 40px;
                            text-align: center;
                            font-size: 14px;
                            color: #777;
                        }
                                
                        header {
                            margin-bottom: 20px;
                            text-align: center;
                        }
                    </style>
                </head>
                                
                <body>
                <div class="user-info">
                    <p class="label">First Name:</p>
                    <p class="value">%s</p>
                                
                    <p class="label">Last Name:</p>
                    <p class="value">%s</p>
                                
                    <p class="label">Email:</p>
                    <p class="value">%s</p>
                                
                    <p class="label">Provider:</p>
                    <p class="value">%s</p>
                </div>
                                
                <footer>
                    Report generated on %s
                </footer>
                </body>
                </html>
                """.formatted(user.getFirstName(), user.getLastName(), user.getEmail(), user.getProvider().name(), new DateTime().toDate().toString());

    }
}
