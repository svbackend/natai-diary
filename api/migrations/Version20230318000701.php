<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

final class Version20230318000701 extends AbstractMigration
{

    public function up(Schema $schema): void
    {
        $this->addSql('CREATE SEQUENCE feedback_id_seq INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('CREATE TABLE feedback (id INT NOT NULL, user_id UUID DEFAULT NULL, content TEXT NOT NULL, stars INT DEFAULT NULL, created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_D2294458A76ED395 ON feedback (user_id)');
        $this->addSql('COMMENT ON COLUMN feedback.user_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN feedback.created_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('CREATE TABLE suggestion (id UUID NOT NULL, user_id UUID DEFAULT NULL, prompt_id UUID NOT NULL, notes_ids JSON NOT NULL, date_from DATE NOT NULL, date_to DATE NOT NULL, input TEXT NOT NULL, output TEXT NOT NULL, is_received BOOLEAN NOT NULL, feedback_rating INT DEFAULT NULL, created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, usage JSON NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_DD80F31BA76ED395 ON suggestion (user_id)');
        $this->addSql('CREATE INDEX IDX_DD80F31BB5C4AA38 ON suggestion (prompt_id)');
        $this->addSql('COMMENT ON COLUMN suggestion.id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN suggestion.user_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN suggestion.prompt_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN suggestion.date_from IS \'(DC2Type:date_immutable)\'');
        $this->addSql('COMMENT ON COLUMN suggestion.date_to IS \'(DC2Type:date_immutable)\'');
        $this->addSql('COMMENT ON COLUMN suggestion.created_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('CREATE TABLE suggestion_prompt (id UUID NOT NULL, system_prompt TEXT NOT NULL, user_prompt_prefix TEXT DEFAULT NULL, user_prompt_suffix TEXT DEFAULT NULL, PRIMARY KEY(id))');
        $this->addSql('COMMENT ON COLUMN suggestion_prompt.id IS \'(DC2Type:uuid)\'');
        $this->addSql('ALTER TABLE feedback ADD CONSTRAINT FK_D2294458A76ED395 FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE suggestion ADD CONSTRAINT FK_DD80F31BA76ED395 FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE suggestion ADD CONSTRAINT FK_DD80F31BB5C4AA38 FOREIGN KEY (prompt_id) REFERENCES suggestion_prompt (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP SEQUENCE feedback_id_seq CASCADE');
        $this->addSql('ALTER TABLE feedback DROP CONSTRAINT FK_D2294458A76ED395');
        $this->addSql('ALTER TABLE suggestion DROP CONSTRAINT FK_DD80F31BA76ED395');
        $this->addSql('ALTER TABLE suggestion DROP CONSTRAINT FK_DD80F31BB5C4AA38');
        $this->addSql('DROP TABLE feedback');
        $this->addSql('DROP TABLE suggestion');
        $this->addSql('DROP TABLE suggestion_prompt');
    }
}
