package com.peakform.claudinary.service;

import org.springframework.stereotype.Service;

@Service
public class AvatarService {

    public String generateDefaultAvatarUrl(String username, String email) {
        String initials = getInitials(username);
        String color = generateColorFromEmail(email);

        return "https://ui-avatars.com/api/?name=" + initials +
                "&background=" + color + "&color=fff&size=128";
    }

    private String getInitials(String username) {
        if (username == null || username.isEmpty()) return "US";

        String[] parts = username.split("[^a-zA-Z]");
        if (parts.length == 0) return "US";

        String initials = "";
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                initials += parts[i].charAt(0);
            }
        }

        return initials.toUpperCase();
    }

    private String generateColorFromEmail(String email) {
        if (email == null) return "6c757d";

        int hash = email.hashCode();
        int color = Math.abs(hash % 0xFFFFFF);
        return String.format("%06x", color);
    }

}
