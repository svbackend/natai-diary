<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

final class Version20230325225016 extends AbstractMigration
{

    public function up(Schema $schema): void
    {
        $this->addSql('CREATE SEQUENCE blog_article_translation_id_seq INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('CREATE TABLE blog_article (id UUID NOT NULL, short_id INT NOT NULL, status VARCHAR(255) NOT NULL, cover VARCHAR(255) NOT NULL, created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, updated_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_EECCB3E5F8496E51 ON blog_article (short_id)');
        $this->addSql('COMMENT ON COLUMN blog_article.id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN blog_article.created_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('COMMENT ON COLUMN blog_article.updated_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('CREATE TABLE blog_article_image (id UUID NOT NULL, article_id UUID NOT NULL, attachment_id UUID NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_C10CBC227294869C ON blog_article_image (article_id)');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_C10CBC22464E68B ON blog_article_image (attachment_id)');
        $this->addSql('COMMENT ON COLUMN blog_article_image.id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN blog_article_image.article_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN blog_article_image.attachment_id IS \'(DC2Type:uuid)\'');
        $this->addSql('CREATE TABLE blog_article_translation (id INT NOT NULL, article_id UUID NOT NULL, locale VARCHAR(255) NOT NULL, slug VARCHAR(255) NOT NULL, title VARCHAR(255) NOT NULL, content TEXT NOT NULL, meta_keywords VARCHAR(255) NOT NULL, meta_description VARCHAR(255) NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_6CFC2FBD989D9B62 ON blog_article_translation (slug)');
        $this->addSql('CREATE INDEX IDX_6CFC2FBD7294869C ON blog_article_translation (article_id)');
        $this->addSql('COMMENT ON COLUMN blog_article_translation.article_id IS \'(DC2Type:uuid)\'');
        $this->addSql('ALTER TABLE blog_article_image ADD CONSTRAINT FK_C10CBC227294869C FOREIGN KEY (article_id) REFERENCES blog_article (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE blog_article_image ADD CONSTRAINT FK_C10CBC22464E68B FOREIGN KEY (attachment_id) REFERENCES uploaded_attachment (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE blog_article_translation ADD CONSTRAINT FK_6CFC2FBD7294869C FOREIGN KEY (article_id) REFERENCES blog_article (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP SEQUENCE blog_article_translation_id_seq CASCADE');
        $this->addSql('ALTER TABLE blog_article_image DROP CONSTRAINT FK_C10CBC227294869C');
        $this->addSql('ALTER TABLE blog_article_image DROP CONSTRAINT FK_C10CBC22464E68B');
        $this->addSql('ALTER TABLE blog_article_translation DROP CONSTRAINT FK_6CFC2FBD7294869C');
        $this->addSql('DROP TABLE blog_article');
        $this->addSql('DROP TABLE blog_article_image');
        $this->addSql('DROP TABLE blog_article_translation');
    }
}
