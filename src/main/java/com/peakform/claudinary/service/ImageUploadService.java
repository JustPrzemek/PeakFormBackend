package com.peakform.claudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.peakform.exceptions.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try{
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "profile_pics"));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload image to Cloudinary", e);
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (IOException e) {
            System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
        }
    }

    //tutaj sobie rozpisalem co co robi bo nie za bardzo bede pamiental
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // Przykład URL: https://res.cloudinary.com/demo/image/upload/v1234567/profile_pics/abc123.jpg
            // Public ID: profile_pics/abc123
            URL url = new URL(imageUrl);
            String path = url.getPath();

            // Szukamy części po "upload/"
            String[] parts = path.split("/upload/");
            if (parts.length > 1) {
                String publicIdWithVersion = parts[1];
                // Usuwamy wersję (v1234567/) jeśli istnieje
                String[] publicIdParts = publicIdWithVersion.split("/", 2);
                if (publicIdParts.length > 1) {
                    // Usuwamy extension (.jpg, .png etc.)
                    String publicId = publicIdParts[1];
                    int lastDotIndex = publicId.lastIndexOf('.');
                    if (lastDotIndex > 0) {
                        return publicId.substring(0, lastDotIndex);
                    }
                    return publicId;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
