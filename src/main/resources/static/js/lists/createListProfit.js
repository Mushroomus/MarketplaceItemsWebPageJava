$(document).ready(function() {
    document.getElementById("generatePdf").addEventListener("click", function () {

        window.jsPDF = window.jspdf.jsPDF;

        var doc = new jsPDF();

        doc.setFont("helvetica");

        doc.setFontSize(18);
        doc.text("Generated Profit List", 14, 10);

        var date = new Date().toLocaleDateString();
        doc.setFontSize(14);
        doc.text(date, 14, 18);

        doc.autoTable({
            html: "#profitTable",
            startY: 20,
            headStyles: {
                fillColor: [211, 211, 211],
                textColor: [0, 0, 0]
            },
        });
        doc.save("generated-table.pdf");
    });
});

