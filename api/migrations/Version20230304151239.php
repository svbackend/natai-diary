<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

final class Version20230304151239 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE uploaded_attachment ADD created_at TIMESTAMP(0) WITHOUT TIME ZONE DEFAULT NULL');
        $this->addSql('UPDATE uploaded_attachment SET created_at = NOW()');
        $this->addSql('ALTER TABLE uploaded_attachment ALTER created_at SET NOT NULL');
        $this->addSql('COMMENT ON COLUMN uploaded_attachment.created_at IS \'(DC2Type:datetime_immutable)\'');

        $this->addSql('ALTER TABLE pending_attachment ADD original_filename VARCHAR(255) DEFAULT NULL');
        $this->addSql('UPDATE pending_attachment SET original_filename = key');
        $this->addSql('ALTER TABLE pending_attachment ALTER original_filename SET NOT NULL');

        $this->addSql('ALTER TABLE uploaded_attachment ADD original_filename VARCHAR(255) DEFAULT NULL');
        $this->addSql('UPDATE uploaded_attachment SET original_filename = key');
        $this->addSql('ALTER TABLE uploaded_attachment ALTER original_filename SET NOT NULL');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('ALTER TABLE uploaded_attachment DROP created_at');
        $this->addSql('ALTER TABLE pending_attachment DROP original_filename');
        $this->addSql('ALTER TABLE uploaded_attachment DROP original_filename');
    }
}
