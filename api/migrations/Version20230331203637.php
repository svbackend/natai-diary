<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

final class Version20230331203637 extends AbstractMigration
{

    public function up(Schema $schema): void
    {
        $this->addSql('CREATE TABLE suggestion_context (id UUID NOT NULL, suggestion_id UUID DEFAULT NULL, context TEXT NOT NULL, usage JSON NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_8D87C60AA41BB822 ON suggestion_context (suggestion_id)');
        $this->addSql('COMMENT ON COLUMN suggestion_context.id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN suggestion_context.suggestion_id IS \'(DC2Type:uuid)\'');
        $this->addSql('ALTER TABLE suggestion_context ADD CONSTRAINT FK_8D87C60AA41BB822 FOREIGN KEY (suggestion_id) REFERENCES suggestion (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('ALTER TABLE suggestion_context DROP CONSTRAINT FK_8D87C60AA41BB822');
        $this->addSql('DROP TABLE suggestion_context');
    }
}
