<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

final class Version20230313192743 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $this->addSql('CREATE SEQUENCE attachment_preview_id_seq INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('CREATE TABLE attachment_preview (id INT NOT NULL, attachment_id UUID NOT NULL, key VARCHAR(255) NOT NULL, type VARCHAR(255) NOT NULL, width INT NOT NULL, height INT NOT NULL, created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_71107AB6464E68B ON attachment_preview (attachment_id)');
        $this->addSql('COMMENT ON COLUMN attachment_preview.attachment_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN attachment_preview.created_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('ALTER TABLE attachment_preview ADD CONSTRAINT FK_71107AB6464E68B FOREIGN KEY (attachment_id) REFERENCES uploaded_attachment (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP SEQUENCE attachment_preview_id_seq CASCADE');
        $this->addSql('ALTER TABLE attachment_preview DROP CONSTRAINT FK_71107AB6464E68B');
        $this->addSql('DROP TABLE attachment_preview');
    }
}
