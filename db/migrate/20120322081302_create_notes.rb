class CreateNotes < ActiveRecord::Migration
  def change
    create_table :notes do |t|
      t.string :message
      t.integer :user_id
      t.integer :photo_id

      t.timestamps
    end
  end
end
