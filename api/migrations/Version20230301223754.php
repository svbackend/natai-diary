<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20230301223754 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE note_attachment (id UUID NOT NULL, note_id UUID NOT NULL, attachment_id UUID NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_19F8F08B26ED0855 ON note_attachment (note_id)');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_19F8F08B464E68B ON note_attachment (attachment_id)');
        $this->addSql('COMMENT ON COLUMN note_attachment.id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN note_attachment.note_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN note_attachment.attachment_id IS \'(DC2Type:uuid)\'');
        $this->addSql('CREATE TABLE pending_attachment (id UUID NOT NULL, user_id UUID NOT NULL, expires_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, key VARCHAR(255) NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_37E0004A76ED395 ON pending_attachment (user_id)');
        $this->addSql('COMMENT ON COLUMN pending_attachment.id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN pending_attachment.user_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN pending_attachment.expires_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('CREATE TABLE uploaded_attachment (id UUID NOT NULL, user_id UUID NOT NULL, key VARCHAR(255) NOT NULL, metadata JSON NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_23DD9C65A76ED395 ON uploaded_attachment (user_id)');
        $this->addSql('COMMENT ON COLUMN uploaded_attachment.id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN uploaded_attachment.user_id IS \'(DC2Type:uuid)\'');
        $this->addSql('CREATE TABLE messenger_messages (id BIGSERIAL NOT NULL, body TEXT NOT NULL, headers TEXT NOT NULL, queue_name VARCHAR(190) NOT NULL, created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, available_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, delivered_at TIMESTAMP(0) WITHOUT TIME ZONE DEFAULT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_75EA56E0FB7336F0 ON messenger_messages (queue_name)');
        $this->addSql('CREATE INDEX IDX_75EA56E0E3BD61CE ON messenger_messages (available_at)');
        $this->addSql('CREATE INDEX IDX_75EA56E016BA31DB ON messenger_messages (delivered_at)');
        $this->addSql('CREATE OR REPLACE FUNCTION notify_messenger_messages() RETURNS TRIGGER AS $$
            BEGIN
                PERFORM pg_notify(\'messenger_messages\', NEW.queue_name::text);
                RETURN NEW;
            END;
        $$ LANGUAGE plpgsql;');
        $this->addSql('DROP TRIGGER IF EXISTS notify_trigger ON messenger_messages;');
        $this->addSql('CREATE TRIGGER notify_trigger AFTER INSERT OR UPDATE ON messenger_messages FOR EACH ROW EXECUTE PROCEDURE notify_messenger_messages();');
        $this->addSql('ALTER TABLE note_attachment ADD CONSTRAINT FK_19F8F08B26ED0855 FOREIGN KEY (note_id) REFERENCES note (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE note_attachment ADD CONSTRAINT FK_19F8F08B464E68B FOREIGN KEY (attachment_id) REFERENCES uploaded_attachment (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE pending_attachment ADD CONSTRAINT FK_37E0004A76ED395 FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE uploaded_attachment ADD CONSTRAINT FK_23DD9C65A76ED395 FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('ALTER TABLE note_attachment DROP CONSTRAINT FK_19F8F08B26ED0855');
        $this->addSql('ALTER TABLE note_attachment DROP CONSTRAINT FK_19F8F08B464E68B');
        $this->addSql('ALTER TABLE pending_attachment DROP CONSTRAINT FK_37E0004A76ED395');
        $this->addSql('ALTER TABLE uploaded_attachment DROP CONSTRAINT FK_23DD9C65A76ED395');
        $this->addSql('DROP TABLE note_attachment');
        $this->addSql('DROP TABLE pending_attachment');
        $this->addSql('DROP TABLE uploaded_attachment');
        $this->addSql('DROP TABLE messenger_messages');
    }
}
