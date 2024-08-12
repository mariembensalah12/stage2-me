export class Form {
  constructor(
    public id?: number,
    public nom?: string,
    public champform?: { [key: string]: any }, // Utilisez un type qui correspond à Map<String, Object> en Java
    public xmlOutput?: string,
    public output?: string // Assurez-vous que cela correspond à la structure JSON attendue
  ) {}
}
