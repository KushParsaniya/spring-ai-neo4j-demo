# Spring AI Neo4j ETL Pipeline

Welcome to **Spring AI Neo4j ETL Pipeline** — a powerful Spring Boot-based project that demonstrates a complete **ETL (Extract, Transform, Load)** pipeline for building a **knowledge graph** or **vector database** from PDFs or text documents.

This project uses **Groq OpenAI** and **Local Ollama** models for **text extraction**, **chunking**, **metadata enrichment** (like keywords, summaries), and **vector embedding** using **Nomic Embedding models**.

---

## ✨ Features

- **Extract** text from uploaded PDFs or text files.
- **Transform** extracted text into meaningful **chunks**.
- **Enrich** each chunk with rich **metadata**:
  - Auto-generated **keywords**.
  - **Summary** of the **previous**, **current**, and **next** documents.
- **Embed** enriched chunks using:
  - **Nomic Embeddings** (via Local Ollama).
  - Or your choice of embedding models.
- **Load** final enriched vector data into a **Vector Database** (Neo4j or others).
- Supports:
  - **Groq OpenAI** models for chat-based metadata generation.
  - **Local Ollama** for offline metadata generation and embeddings.
- **Batch Processing**:
  - The pipeline supports batch processing, allowing it to **continue from where it left off** after a failure or interruption. This feature ensures reliability and robustness during large document processing.

---

## 🛠 Tech Stack

- **Spring Boot 3**
- **Spring AI**
- **Groq OpenAI API**
- **Local Ollama** (Nomic Embedding + Local Chat Models)
- **Neo4j** (Vector Database)
- **PDFBox** / **Apache Tika** (for PDF extraction)
- **Spring Batch** (for batch processing)
- **Gradle** (for build and dependency management)
- **Java 21**

---

## 📋 How the ETL Pipeline Works

1. **Extract**:
   - Upload a **PDF** or **text** file.
   - Extract plain text from the input.

2. **Transform**:
   - Split the extracted text into **logical chunks**.
   - Preprocess the text if needed.

3. **Enrich**:
   - For each chunk:
     - Generate **keywords**.
     - Summarize the **current**, **previous**, and **next** sections.
     - Use **Groq OpenAI** or **Local Ollama** models for generation.

4. **Embed**:
   - Convert each enriched chunk into **vector embeddings**.
   - Use **Nomic Embedding model** (via Ollama) or alternatives.

5. **Load**:
   - Store the enriched chunks and their embeddings into the **Neo4j** vector database.

6. **Batch Processing**:
   - The pipeline is designed to handle interruptions gracefully. It can resume the processing from where it left off, ensuring that long-running document processing tasks are not disrupted by failures or system restarts.

---

## 🚀 How to Run Locally

1. **Clone the repo**:

   ```bash
   git clone https://github.com/KushParsaniya/spring-ai-neo4j.git
   cd spring-ai-neo4j
   ```

2. **Configure `application.properties`**:

   Set your Groq OpenAI keys, Ollama model details, Neo4j connection info, and Spring Batch settings.

3. **Build the project with Gradle**:

   ```bash
   ./gradlew build
   ```

4. **Run the application**:

   ```bash
   ./gradlew bootRun
   ```

5. **Upload PDFs** via API or frontend and watch the pipeline in action!

---

## 📚 Prerequisites

- Java 21+
- Gradle 7+
- Local Ollama server running (for Nomic Embeddings and/or local chat models)
- Groq OpenAI API access (optional if using only local models)
- Running Neo4j instance (vector support enabled)
- **Spring Batch** configured for batch processing

---

## 📈 Future Enhancements

- Full UI for uploading documents and visualizing the knowledge graph.
- Support for multiple chunking strategies.
- Automatic scheduling for ETL on document uploads.
- Completing the **batch processing feature** to ensure seamless operation across large-scale data loads and interruptions.

---

## 🤝 Contributing

Feel free to fork the project, raise issues, or submit pull requests to improve it!


## 🙏 Acknowledgements

- [Spring AI](https://spring.io/projects/spring-ai)
- [Ollama](https://ollama.com/)
- [Groq](https://groq.com/)
- [Neo4j](https://neo4j.com/)
- [Nomic Embeddings](https://nomic.ai/)
