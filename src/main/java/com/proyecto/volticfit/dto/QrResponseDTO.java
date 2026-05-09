package com.proyecto.volticfit.dto;

/**
 * DTO utilizado para enviar al cliente la información necesaria
 * para generar el código QR.
 */
public class QrResponseDTO {

    private String qrData;

    /**
     * Constructor del DTO.
     *
     * @param qrData contenido que será representado como QR (URL con token)
     */
    public QrResponseDTO(String qrData) {
        this.qrData = qrData;
    }

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }
}
