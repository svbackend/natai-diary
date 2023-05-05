<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

final class Version20230505121410 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $this->addSql('CREATE TABLE category (id SERIAL NOT NULL, name VARCHAR(255) NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_64C19C15E237E06 ON category (name)');
        $this->addSql('CREATE TABLE link (id SERIAL NOT NULL, url VARCHAR(255) NOT NULL, title VARCHAR(255) NOT NULL, description VARCHAR(255) NOT NULL, image VARCHAR(255) DEFAULT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_36AC99F1F47645AE ON link (url)');
        $this->addSql('CREATE TABLE link_category (id SERIAL NOT NULL, link_id INT NOT NULL, category_id INT NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_CBE67908ADA40271 ON link_category (link_id)');
        $this->addSql('CREATE INDEX IDX_CBE6790812469DE2 ON link_category (category_id)');
        $this->addSql('CREATE TABLE suggestion_link (id SERIAL NOT NULL, suggestion_id UUID NOT NULL, link_id INT NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_3DC956E2A41BB822 ON suggestion_link (suggestion_id)');
        $this->addSql('CREATE INDEX IDX_3DC956E2ADA40271 ON suggestion_link (link_id)');
        $this->addSql('COMMENT ON COLUMN suggestion_link.suggestion_id IS \'(DC2Type:uuid)\'');
        $this->addSql('ALTER TABLE link_category ADD CONSTRAINT FK_CBE67908ADA40271 FOREIGN KEY (link_id) REFERENCES link (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE link_category ADD CONSTRAINT FK_CBE6790812469DE2 FOREIGN KEY (category_id) REFERENCES category (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE suggestion_link ADD CONSTRAINT FK_3DC956E2A41BB822 FOREIGN KEY (suggestion_id) REFERENCES suggestion (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE suggestion_link ADD CONSTRAINT FK_3DC956E2ADA40271 FOREIGN KEY (link_id) REFERENCES link (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('SELECT setval(\'api_token_id_seq\', (SELECT MAX(id) FROM api_token))');
        $this->addSql('ALTER TABLE api_token ALTER id SET DEFAULT nextval(\'api_token_id_seq\')');
        $this->addSql('SELECT setval(\'attachment_preview_id_seq\', (SELECT MAX(id) FROM attachment_preview))');
        $this->addSql('ALTER TABLE attachment_preview ALTER id SET DEFAULT nextval(\'attachment_preview_id_seq\')');
        $this->addSql('SELECT setval(\'blog_article_translation_id_seq\', (SELECT MAX(id) FROM blog_article_translation))');
        $this->addSql('ALTER TABLE blog_article_translation ALTER id SET DEFAULT nextval(\'blog_article_translation_id_seq\')');
        $this->addSql('SELECT setval(\'feedback_id_seq\', (SELECT MAX(id) FROM feedback))');
        $this->addSql('ALTER TABLE feedback ALTER id SET DEFAULT nextval(\'feedback_id_seq\')');
        $this->addSql('SELECT setval(\'user_log_id_seq\', (SELECT MAX(id) FROM user_log))');
        $this->addSql('ALTER TABLE user_log ALTER id SET DEFAULT nextval(\'user_log_id_seq\')');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('ALTER TABLE link_category DROP CONSTRAINT FK_CBE67908ADA40271');
        $this->addSql('ALTER TABLE link_category DROP CONSTRAINT FK_CBE6790812469DE2');
        $this->addSql('ALTER TABLE suggestion_link DROP CONSTRAINT FK_3DC956E2A41BB822');
        $this->addSql('ALTER TABLE suggestion_link DROP CONSTRAINT FK_3DC956E2ADA40271');
        $this->addSql('DROP TABLE category');
        $this->addSql('DROP TABLE link');
        $this->addSql('DROP TABLE link_category');
        $this->addSql('DROP TABLE suggestion_link');
        $this->addSql('ALTER TABLE blog_article_translation ALTER id DROP DEFAULT');
        $this->addSql('ALTER TABLE api_token ALTER id DROP DEFAULT');
        $this->addSql('ALTER TABLE attachment_preview ALTER id DROP DEFAULT');
        $this->addSql('ALTER TABLE user_log ALTER id DROP DEFAULT');
        $this->addSql('ALTER TABLE feedback ALTER id DROP DEFAULT');
    }
}
