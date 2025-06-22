package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;

@Entity
public class Subtopic {
    @Id
    private String name; // e.g., Arrays, SQL Queries

    @ManyToOne
    @JoinColumn(name = "subject_name") // Explicitly specify the foreign key column
    private Subject subject;

    @Lob
    private byte[] pdfMaterial; // Store PDF as a byte array

    private String referenceLink; // HTTP reference link

    // Constructors
    public Subtopic() {
    }

    public Subtopic(String name, Subject subject, byte[] pdfMaterial, String referenceLink) {
        this.name = name;
        this.subject = subject;
        this.pdfMaterial = pdfMaterial;
        this.referenceLink = referenceLink;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public byte[] getPdfMaterial() {
        return pdfMaterial;
    }

    public void setPdfMaterial(byte[] pdfMaterial) {
        this.pdfMaterial = pdfMaterial;
    }

    public String getReferenceLink() {
        return referenceLink;
    }

    public void setReferenceLink(String referenceLink) {
        this.referenceLink = referenceLink;
    }
}