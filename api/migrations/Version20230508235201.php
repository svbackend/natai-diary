<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20230508235201 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $this->addSql('CREATE TABLE user_billing_history (id SERIAL NOT NULL, user_id UUID NOT NULL, feature VARCHAR(255) NOT NULL, operation VARCHAR(255) NOT NULL, amount INT NOT NULL, currency VARCHAR(3) NOT NULL, created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_5E489713A76ED395 ON user_billing_history (user_id)');
        $this->addSql('COMMENT ON COLUMN user_billing_history.user_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN user_billing_history.created_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('CREATE TABLE user_feature (id SERIAL NOT NULL, user_id UUID NOT NULL, feature VARCHAR(255) NOT NULL, created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_47DA6B6CA76ED395 ON user_feature (user_id)');
        $this->addSql('COMMENT ON COLUMN user_feature.user_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN user_feature.created_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('ALTER TABLE user_billing_history ADD CONSTRAINT FK_5E489713A76ED395 FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE user_feature ADD CONSTRAINT FK_47DA6B6CA76ED395 FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('ALTER TABLE user_billing_history DROP CONSTRAINT FK_5E489713A76ED395');
        $this->addSql('ALTER TABLE user_feature DROP CONSTRAINT FK_47DA6B6CA76ED395');
        $this->addSql('DROP TABLE user_billing_history');
        $this->addSql('DROP TABLE user_feature');
    }
}
