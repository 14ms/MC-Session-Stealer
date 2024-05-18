package com.github.shurpe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DiscordWebhook {

    private final String url;
    private String content, username, avatarUrl;
    private boolean tts;

    private final List<EmbedObject> embeds = new ArrayList<>();

    public DiscordWebhook(String url) {
        this.url = url;
    }

    public DiscordWebhook setContent(String content) {
        this.content = content;
        return this;
    }

    public DiscordWebhook setUsername(String username) {
        this.username = username;
        return this;
    }

    public DiscordWebhook setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public DiscordWebhook setTts(boolean tts) {
        this.tts = tts;
        return this;
    }

    public DiscordWebhook addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
        return this;
    }

    public void execute() throws IOException {
        JsonObject json = new JsonObject();

        json.addProperty("content", this.content);
        json.addProperty("username", this.username);
        json.addProperty("avatar_url", this.avatarUrl);
        json.addProperty("tts", this.tts);

        if (!this.embeds.isEmpty()) {
            JsonArray jsonEmbeds = new JsonArray();

            for (EmbedObject embed : this.embeds) {
                JsonObject jsonEmbed = new JsonObject();

                jsonEmbed.addProperty("title", embed.getTitle());
                jsonEmbed.addProperty("description", embed.getDescription());
                jsonEmbed.addProperty("url", embed.getUrl());

                if (embed.getColor() != -1)
                    jsonEmbed.addProperty("color", embed.getColor());

                EmbedObject.Footer footer = embed.getFooter();
                EmbedObject.Image image = embed.getImage();
                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                EmbedObject.Author author = embed.getAuthor();
                List<EmbedObject.Field> fields = embed.getFields();

                if (footer != null) {
                    JsonObject jsonFooter = new JsonObject();

                    jsonFooter.addProperty("text", footer.getText());
                    jsonFooter.addProperty("icon_url", footer.getIconUrl());
                    jsonEmbed.add("footer", jsonFooter);
                }

                if (image != null) {
                    JsonObject jsonImage = new JsonObject();

                    jsonImage.addProperty("url", image.getUrl());
                    jsonEmbed.add("image", jsonImage);
                }

                if (thumbnail != null) {
                    JsonObject jsonThumbnail = new JsonObject();

                    jsonThumbnail.addProperty("url", thumbnail.getUrl());
                    jsonEmbed.add("thumbnail", jsonThumbnail);
                }

                if (author != null) {
                    JsonObject jsonAuthor = new JsonObject();

                    jsonAuthor.addProperty("name", author.getName());
                    jsonAuthor.addProperty("url", author.getUrl());
                    jsonAuthor.addProperty("icon_url", author.getIconUrl());
                    jsonEmbed.add("author", jsonAuthor);
                }

                JsonArray jsonFields = new JsonArray();
                for (EmbedObject.Field field : fields) {
                    JsonObject jsonField = new JsonObject();

                    jsonField.addProperty("name", field.getName());
                    jsonField.addProperty("value", field.getValue());
                    jsonField.addProperty("inline", field.isInline());

                    jsonFields.add(jsonField);
                }
                jsonEmbed.add("fields", jsonFields);

                jsonEmbeds.add(jsonEmbed);
            }

            json.add("embeds", jsonEmbeds);
        }

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(this.url);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(new StringEntity(json.toString()));

            httpclient.execute(httpPost);
        }
    }

    public static class EmbedObject {

        private String title, description, url;
        private int color = -1;

        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;

        private final List<Field> fields = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getUrl() {
            return url;
        }

        public int getColor() {
            return color;
        }

        public Footer getFooter() {
            return footer;
        }

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public Image getImage() {
            return image;
        }

        public Author getAuthor() {
            return author;
        }

        public List<Field> getFields() {
            return fields;
        }

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedObject setColor(int color) {
            this.color = color;
            return this;
        }

        public EmbedObject setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
            return this;
        }

        public EmbedObject setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public EmbedObject setImage(String url) {
            this.image = new Image(url);
            return this;
        }

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        private static class Footer {

            private final String text, iconUrl;

            private Footer(String text, String iconUrl) {
                this.text = text;
                this.iconUrl = iconUrl;
            }

            private String getText() {
                return text;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }

        private static class Thumbnail {

            private final String url;

            private Thumbnail(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private static class Image {

            private final String url;

            private Image(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private static class Author {

            private final String name, url, iconUrl;

            private Author(String name, String url, String iconUrl) {
                this.name = name;
                this.url = url;
                this.iconUrl = iconUrl;
            }

            private String getName() {
                return name;
            }

            private String getUrl() {
                return url;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }

        private static class Field {

            private final String name, value;
            private final boolean inline;

            private Field(String name, String value, boolean inline) {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            private String getName() {
                return name;
            }

            private String getValue() {
                return value;
            }

            private boolean isInline() {
                return inline;
            }
        }
    }
}