import os

# Configura l'estensione dei file (es. .java, .py, .cpp)
EXTENSION = ".java"  # Cambia in base alle tue esigenze
OUTPUT_FILE = "codice_unificato.txt"

with open(OUTPUT_FILE, 'w', encoding='utf-8') as outfile:
    for root, dirs, files in os.walk("."):
        for file in files:
            if file.endswith(EXTENSION):
                path = os.path.join(root, file)
                with open(path, 'r', encoding='utf-8') as infile:
                    outfile.write(f"\n\n// ---- Inizio: {path} ----\n")
                    outfile.write(infile.read())
                    outfile.write(f"\n// ---- Fine: {path} ----\n")

