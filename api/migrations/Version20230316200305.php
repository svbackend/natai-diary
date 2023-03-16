<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

final class Version20230316200305 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $this->addSql('CREATE TABLE suggestion (id UUID NOT NULL, user_id UUID DEFAULT NULL, notes_ids JSON NOT NULL, date_from DATE NOT NULL, date_to DATE NOT NULL, prompt TEXT NOT NULL, input TEXT NOT NULL, output TEXT NOT NULL, is_received BOOLEAN NOT NULL, feedback_rating INT DEFAULT NULL, created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_DD80F31BA76ED395 ON suggestion (user_id)');
        $this->addSql('COMMENT ON COLUMN suggestion.id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN suggestion.user_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN suggestion.date_from IS \'(DC2Type:date_immutable)\'');
        $this->addSql('COMMENT ON COLUMN suggestion.date_to IS \'(DC2Type:date_immutable)\'');
        $this->addSql('COMMENT ON COLUMN suggestion.created_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('ALTER TABLE suggestion ADD CONSTRAINT FK_DD80F31BA76ED395 FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('ALTER TABLE suggestion DROP CONSTRAINT FK_DD80F31BA76ED395');
        $this->addSql('DROP TABLE suggestion');
    }
}
