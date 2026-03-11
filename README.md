# Java Practice – Fourth Step (2026)

このリポジトリは  
**Java エンジニア復帰の第4段階の学習記録**です。

[ThirdStep](https://github.com/tkelie/Java_2026ThirdStep) で身につけた  
Spring Boot REST API・Service 層分離・MockMvc テスト・エラーハンドリングをベースに、

**DB 永続化・セッション分析 API・API ドキュメント・Docker 化・Spring Security による認証**

を学習します。  
ThirdStep のインメモリ実装を本格的な Web アプリとして再構築しながら、  
FirstStep で手書きしたセッション計算ロジック（LOGIN〜LOGOUT のペア集計）を  
REST API として公開し、実務で頻出の技術スタックを体系的に習得することが目標です。

---

# 🔗 学習の流れ

| リポジトリ | テーマ |
|---|---|
| [FirstStep](https://github.com/tkelie/Java_2026FirstStep) | Java 8+ 構文（Stream / Optional / java.time）|
| [SecondStep](https://github.com/tkelie/Java_2026SecondStep) | JUnit・例外設計・責務分離・CLI ツール化 |
| [ThirdStep](https://github.com/tkelie/Java_2026ThirdStep) | Spring Boot REST API 入門 |
| **FourthStep（本リポジトリ）** | **DB 永続化・セッション分析・Swagger・Docker・Spring Security** |

---

# 🎯 学習の目的

- ログフォーマットを拡張し `log_time`（TIMESTAMP）と `action`（enum 管理）で再設計できる
- Spring Data JPA を使った DB 永続化（H2 → PostgreSQL）ができる
- Flyway でスキーマのバージョン管理ができる
- LOGIN〜LOGOUT のペアからセッションを抽出し、集計 API として公開できる
- 不完全セッション（LOGOUT なし ＝ ログイン中）の検出ができる
- springdoc-openapi で API ドキュメントを自動生成できる
- Docker / docker-compose でアプリと DB をコンテナ化できる
- Spring Security で Basic 認証・JWT 認証を実装できる
- `@DataJpaTest` / Testcontainers で DB 層のテストが書ける
- ThirdStep の `record Log` を `@Entity` クラスに移行できる

---

# 📂 ディレクトリ構成

```
.
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/Java_2026FourthStep/
│   │   │       ├── Java2026FourthStepApplication.java
│   │   │       ├── controller/
│   │   │       │   ├── LogController.java
│   │   │       │   └── SessionController.java
│   │   │       ├── service/
│   │   │       │   ├── LogService.java
│   │   │       │   └── SessionService.java
│   │   │       ├── repository/
│   │   │       │   └── LogRepository.java
│   │   │       ├── entity/
│   │   │       │   ├── Log.java
│   │   │       │   └── Action.java
│   │   │       ├── dto/
│   │   │       │   ├── LogRequest.java
│   │   │       │   ├── SessionDto.java
│   │   │       │   └── SessionSummaryDto.java
│   │   │       └── exception/
│   │   │           ├── UserNotFoundException.java
│   │   │           ├── ErrorResponse.java
│   │   │           └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/example/Java_2026FourthStep/
│               ├── Java2026FourthStepApplicationTests.java
│               └── LogControllerTest.java
├── pom.xml
└── README.md
```

---

# 🧩 練習ロードマップ

| STEP | テーマ | 実施日 | 状態 |
|---|---|---|---|
| STEP 1 | H2 + JPA 導入・CRUD 実装 | 2026/03/10 | ✅ |
| STEP 2 | セッション分析 API | 2026/03/11 | ✅ |
| STEP 3 | PostgreSQL 移行・Flyway 導入 | - | 🔲 |
| STEP 4 | Swagger / OpenAPI ドキュメント生成 | - | 🔲 |
| STEP 5 | Docker 化（Dockerfile + docker-compose）| - | 🔲 |
| STEP 6 | Spring Security（Basic 認証）| - | 🔲 |
| STEP 7 | JWT 認証への移行 | - | 🔲 |
| STEP 8 | DB 層テスト（@DataJpaTest / Testcontainers）| - | 🔲 |

---

# 📝 各 STEP の詳細

---

## STEP 1：H2 + JPA 導入・CRUD 実装【2026/03/10実施】

### 🌱 A-1：インメモリ実装から JPA へ・ログフォーマット拡張

- ThirdStep の `record Log` を `@Entity` クラスに変更
- ログフォーマットを拡張し、`date` + `time` を `log_time`（`LocalDateTime`）に統合
- `action` 列を `String` から `enum Action { LOGIN, LOGOUT }` で管理
- `JpaRepository<Log, Long>` を継承した `LogRepository` に差し替え
- POST リクエスト受け口として `LogRequest` DTO を追加
- H2 インメモリ DB で動作確認（アプリ再起動でリセット）

**ThirdStep からの主な変更点**

| ThirdStep | FourthStep |
|---|---|
| `record Log(String userId, String action)` | `@Entity class Log` |
| `action` が `String` | `action` が `enum Action { LOGIN, LOGOUT }` |
| 日時情報なし | `log_time`（`LocalDateTime`）を追加 |
| `List<Log>` をインメモリ保持 | `JpaRepository` 経由で DB 操作 |
| `@RequestBody Log` で POST 受け取り | `@RequestBody LogRequest` DTO 経由に分離 |

**`Log` エンティティの概要**

```java
@Entity
@Table(name = "logs")
public class Log {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Enumerated(EnumType.STRING)
    private Action action;          // LOGIN / LOGOUT

    private LocalDateTime logTime;  // 日付 + 時刻を統合
}
```

**学習内容**

- `@Entity` / `@Id` / `@GeneratedValue` / `@Enumerated`
- `JpaRepository` の CRUD メソッド（`save` / `findAll` / `findById` / `deleteById`）
- メソッド名によるクエリ自動生成（`findByUserId`）
- `spring.jpa.show-sql=true` でクエリのログ確認
- H2 コンソール（`/h2-console`）でデータ確認
- `record` を DTO として使う（`@Entity` とは分離）

> ※ `record` は不変オブジェクトのため JPA の `@Entity` として使用不可。  
> 通常の getter/setter クラスに移行する

---

## STEP 2：セッション分析 API【2026/03/11実施】

### 🌱 B-1：SecondStep のセッション計算ロジックを REST API として公開

SecondStep で Java プログラムとして実装した  
「LOGIN〜LOGOUT のペアを抽出してセッション時間を集計する」ロジックを、  
Spring Boot の Service 層に移植して API として公開する。

**実装する API**

| エンドポイント | 説明 |
|---|---|
| `GET /api/sessions/{userId}` | ユーザーのセッション一覧（開始・終了・滞在時間）|
| `GET /api/sessions/ranking` | 総滞在時間ランキング（降順）|
| `GET /api/sessions/average` | 全セッションの平均滞在時間（分・切り捨て）|
| `GET /api/sessions/active` | ログイン中ユーザー（LOGOUT なし）の一覧 |

**FirstStep との対応関係**

| FirstStep（Java プログラム）| FourthStep（API）|
|---|---|
| `extractSessions()` で LOGIN/LOGOUT ペア抽出 | `SessionService.extractSessions()` に移植 |
| `groupingBy + summingLong` で総滞在時間集計 | `GET /api/sessions/ranking` |
| `summarizingLong` で平均計算 | `GET /api/sessions/average` |
| LOGOUT なしを無視 | `GET /api/sessions/active` で「ログイン中」として抽出 |

**セッション抽出ロジックの概要**

```java
// SessionService.java
public List<SessionDto> extractSessions(String userId) {
    var logs = logRepository.findByUserIdOrderByLogTimeAsc(userId);
    List<SessionDto> sessions = new ArrayList<>();

    for (int i = 0; i + 1 < logs.size(); i++) {
        var curr = logs.get(i);
        var next = logs.get(i + 1);

        if (curr.getAction() == Action.LOGIN &&
            next.getAction() == Action.LOGOUT) {

            long minutes = Duration.between(curr.getLogTime(), next.getLogTime()).toMinutes();
            sessions.add(new SessionDto(userId, curr.getLogTime(), next.getLogTime(), minutes));
            i++; // LOGOUT を消費したので次のペアへ
        }
    }
    return sessions;
}
```

**DTO の概要**

```java
// 1セッション分
record SessionDto(
    String userId,
    LocalDateTime loginTime,
    LocalDateTime logoutTime,
    long durationMinutes
) {}

// ランキング用
record SessionSummaryDto(String userId, long totalMinutes) {}
```

**学習内容**

- `LocalDateTime` を使ったタイムスタンプ比較・`Duration.between()`
- Stream API（`groupingBy` / `summingLong` / `summarizingLong`）の Service 層実装
- 不完全セッション（ログイン中）の検出パターン
- カスタムクエリ `findByUserIdOrderByLogTimeAsc`

---

## STEP 3：PostgreSQL 移行・Flyway 導入

### 🌱 C-1：本番想定 DB への切り替えとスキーマ管理

- H2 から PostgreSQL に接続先を変更
- `application-dev.properties` / `application-prod.properties` でプロファイル分離
- Flyway でテーブル定義をマイグレーションファイルで管理

**マイグレーションファイルの例**

```sql
-- V1__create_log_table.sql
CREATE TABLE logs (
    id       BIGSERIAL PRIMARY KEY,
    user_id  VARCHAR(50) NOT NULL,
    action   VARCHAR(10) NOT NULL,
    log_time TIMESTAMP   NOT NULL
);
```

**学習内容**

- `spring.datasource.*` の設定とプロファイル切り替え
- Flyway のバージョン管理（`V1__`, `V2__` のネーミングルール）
- `spring.jpa.hibernate.ddl-auto=validate` で Flyway 管理の DB と整合性確認

---

## STEP 4：Swagger / OpenAPI ドキュメント生成

### 🌱 D-1：API ドキュメントの自動生成

- `springdoc-openapi-starter-webmvc-ui` を導入
- `GET /swagger-ui.html` で API 仕様書をブラウザ確認
- アノテーションでエンドポイントの説明を追加

**アノテーション例**

```java
@Operation(summary = "セッション一覧取得", description = "指定ユーザーの全セッションを返します")
@ApiResponse(responseCode = "200", description = "取得成功")
@ApiResponse(responseCode = "404", description = "ユーザーが存在しない")
@GetMapping("/api/sessions/{userId}")
public List<SessionDto> getSessions(@PathVariable String userId) { ... }
```

**学習内容**

- `@Operation` / `@ApiResponse` / `@Parameter`
- Swagger UI でのリクエスト送信・動作確認
- OpenAPI JSON（`/v3/api-docs`）の構造理解

---

## STEP 5：Docker 化

### 🌱 E-1：アプリと DB をコンテナで管理

- `Dockerfile` で Spring Boot アプリをイメージ化
- `docker-compose.yml` でアプリ + PostgreSQL をまとめて起動

**Dockerfile（マルチステージビルド）**

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml（概要）**

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      SPRING_PROFILES_ACTIVE: prod

  db:
    image: postgres:16
    environment:
      POSTGRES_DB: logdb
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
```

**学習内容**

- `Dockerfile` の書き方（マルチステージビルドで軽量化）
- `docker-compose up` でローカル環境を一発構築
- 環境変数による設定の外部化（`application-prod.properties`）
- コンテナ間通信（`depends_on` / サービス名でのホスト指定）

---

## STEP 6：Spring Security（Basic 認証）

### 🌱 F-1：API エンドポイントの保護

- `spring-boot-starter-security` を導入
- 全 API エンドポイントに認証を要求
- ユーザー情報を DB 管理（`UserDetailsService` 実装）

**学習内容**

- `SecurityFilterChain` の設定
- `@EnableWebSecurity` / `httpBasic()` の有効化
- `UserDetailsService` + `PasswordEncoder`（BCrypt）
- 認証が必要なエンドポイントと公開エンドポイントの使い分け

---

## STEP 7：JWT 認証への移行

### 🌱 G-1：トークンベース認証

- `POST /auth/login` でトークン発行
- リクエストヘッダー `Authorization: Bearer <token>` で認証
- `JwtAuthenticationFilter` を Security チェーンに追加

**認証フロー**

```
クライアント
  → POST /auth/login { username, password }
  ← 200 OK { token: "eyJ..." }

クライアント
  → GET /api/sessions/ranking
    Authorization: Bearer eyJ...
  ← 200 OK [ ... ]
```

**学習内容**

- JWT の構造（Header / Payload / Signature）
- `io.jsonwebtoken:jjwt` ライブラリの使い方
- `OncePerRequestFilter` を使ったトークン検証
- トークンの有効期限設定と例外ハンドリング

---

## STEP 8：DB 層テスト（@DataJpaTest / Testcontainers）

### 🌱 H-1：リポジトリ層の単体テスト

- `@DataJpaTest` で JPA 関連のみをロードした軽量テスト
- Testcontainers で実際の PostgreSQL コンテナを使ったテスト

```java
@DataJpaTest
class LogRepositoryTest {

    @Autowired
    private LogRepository logRepository;

    @Test
    void ユーザーIDでログが時系列順に取得できる() {
        logRepository.save(new Log("alice", Action.LOGIN,
            LocalDateTime.of(2026, 2, 20, 8, 0)));
        logRepository.save(new Log("alice", Action.LOGOUT,
            LocalDateTime.of(2026, 2, 20, 8, 6)));

        var logs = logRepository.findByUserIdOrderByLogTimeAsc("alice");
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).getAction()).isEqualTo(Action.LOGIN);
    }
}
```

**学習内容**

- `@DataJpaTest` によるスライステスト
- `@Sql` アノテーションでテスト用データ投入
- Testcontainers による実 DB を使った統合テスト
- H2 テスト vs Testcontainers の使い分け

---

# 🌐 実装した API 一覧

| メソッド | エンドポイント | 認証 | 説明 |
|---|---|---|---|
| POST | `/auth/login` | 不要 | JWT トークン発行（STEP 7）|
| GET | `/api/logs` | 必要 | 全ログ取得 |
| GET | `/api/logs/{userId}` | 必要 | ユーザー別ログ取得 |
| POST | `/api/logs` | 必要 | ログ登録 |
| GET | `/api/logs/summary` | 必要 | LOGIN 回数集計 |
| GET | `/api/logs/duplicate` | 必要 | 複数回 LOGIN ユーザー |
| GET | `/api/sessions/{userId}` | 必要 | セッション一覧（STEP 2）|
| GET | `/api/sessions/ranking` | 必要 | 総滞在時間ランキング（STEP 2）|
| GET | `/api/sessions/average` | 必要 | 平均セッション時間（STEP 2）|
| GET | `/api/sessions/active` | 必要 | ログイン中ユーザー一覧（STEP 2）|
| GET | `/swagger-ui.html` | 不要 | API ドキュメント（STEP 4）|

---

# 🧠 使用した技術・API

| 機能・ツール | 内容 |
|---|---|
| Spring Boot 4.0.3 | アプリケーションフレームワーク |
| Spring Data JPA | DB 永続化・CRUD 操作 |
| H2 Database | 開発用インメモリ DB |
| PostgreSQL 16 | 本番想定 DB |
| Flyway | DB スキーマのバージョン管理 |
| springdoc-openapi | Swagger UI・OpenAPI ドキュメント自動生成 |
| Spring Security | 認証・認可 |
| jjwt | JWT トークンの生成・検証 |
| Docker / docker-compose | コンテナ化・ローカル環境構築 |
| `@DataJpaTest` | JPA 層スライステスト |
| Testcontainers | 実 DB を使った統合テスト |
| MockMvc | API テスト（ThirdStep 継続）|
| Stream API | `groupingBy` / `summingLong` / `summarizingLong`（SecondStep 継続）|
| `Duration` | セッション時間計算（`java.time` 継続）|
| `enum` | `Action`（LOGIN / LOGOUT）の型安全な管理 |
| `record` | DTO として使用（`LogRequest` / `SessionDto` / `SessionSummaryDto`）|
| Maven | ビルド・依存関係管理 |

---

# 💻 開発環境

- Java 25.0.2
- Spring Boot 4.0.3
- Maven 3.9.12
- PostgreSQL 16
- Docker Desktop
- VSCode
- GitHub
- Windows 11

---

# 🚀 今後の予定（FifthStep 候補）

- フロントエンド連携（React + Vite による SPA）
- CI/CD パイプライン（GitHub Actions）
- クラウドデプロイ（Render / Railway / AWS）
- WebSocket によるリアルタイム通知